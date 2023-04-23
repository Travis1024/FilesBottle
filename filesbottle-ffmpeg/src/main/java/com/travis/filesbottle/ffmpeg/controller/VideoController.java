package com.travis.filesbottle.ffmpeg.controller;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.process.ffmpeg.FFMPEGProcess;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @ClassName VideoController
 * @Description 单独部署，进行视频文件流的处理，此模块可以单独进行限流、流量熔断等操作
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/21
 */
@Slf4j
@RestController
@RequestMapping("/hlsvideo")
public class VideoController {

    @Value("${ffmpeg.filepath}")
    private String videoFilePath;
    @Value("${ffmpeg.path}")
    private String ffmpegPath;
    @Value("${ffmpeg.neturl}")
    private String ffmpegNetUrl;


    @GetMapping("/video")
    public void getVideo(@RequestParam("sourceId") String sourceId, HttpServletResponse response) throws IOException {
        String filePath = videoFilePath + sourceId + "/" + sourceId + ".m3u8";
        log.info(filePath);
        FileReader fileReader = new FileReader(filePath);
        log.info(filePath);
        fileReader.writeToStream(response.getOutputStream());
    }

    @PostMapping("/delete")
    public boolean deleteVideo(@RequestParam("sourceId") String sourceId) {
        // TODO 删除 sourceId 对应的切片文件（ts）及相应的 m3u8 文件
        return true;
    }


    /**
     * @MethodName sliceProcess
     * @Description 上传视频文件、转换格式成 mp4、并进行切片
     * @Author travis-wei
     * @Data 2023/4/23
     * @param multipartFile
     * @Return java.lang.String 返回视频文件的 gridFsId 以供判断请求是否成功
     **/
    @PostMapping("/handle")
    public String sliceProcess(@RequestParam("file") MultipartFile multipartFile) {
        // 获取文件的名称（gridFsId + 文件后缀）
        String originalFilename = multipartFile.getOriginalFilename();
        if (StrUtil.isEmpty(originalFilename)) {
            return null;
        }
        // 获取文件的Context-type
        String contentType = multipartFile.getContentType();
        // 获取视频文件的gridFsId
        String gridFsId = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        // 获取视频文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        // 创建文件夹（文件路径中已经包含了gridFsId）
        // String dirPath = createDir(gridFsId);


        // 临时存储的文件路径
        String tempPath = videoFilePath + originalFilename;
        // 最终mp4存储的文件路径
        String filePath = videoFilePath + gridFsId + ".mp4";
        // ts文件路径
        String tsFilePath = videoFilePath + gridFsId + ".ts";
        // m3u8视频索引路径
        String m3u8FilePath = videoFilePath + gridFsId + ".m3u8";
        // 最终视频切片文件存储的路径
        String tsFinalFilePath = videoFilePath + gridFsId + "-%05d.ts";


        /**
         * 一、保存视频文件
         */
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            fileOutputStream = new FileOutputStream(tempPath);
            IOUtils.copy(inputStream, fileOutputStream);
            fileOutputStream.flush();

        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        }

        /**
         * 二、判断视频文件是否为mp4的格式，不是则转为mp4
         */
        File tempFile = new File(tempPath);
        if (suffix.equalsIgnoreCase("mp4")) {
            // 不需要转换文件格式，只需要移动文件
            tempFile.renameTo(new File(filePath));
        } else {
            boolean formatResult = formatToMp4(tempPath, filePath);
            if (formatResult) tempFile.delete();
        }

        /**
         * 三、进行切片操作（m3u8）
         */
        try {
            // 将mp4格式的文件转换为ts格式
            FFMPEGProcess ffmpeg = new FFMPEGProcess(ffmpegPath);
            ffmpeg.addArgument("-y");
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(filePath);
            ffmpeg.addArgument("-vcodec");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-acodec");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-vbsf");
            ffmpeg.addArgument("h264_mp4toannexb");
            ffmpeg.addArgument(tsFilePath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.info(line);
                }
            }
            // 将ts文件进行切片
            ffmpeg = new FFMPEGProcess(ffmpegPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(tsFilePath);
            ffmpeg.addArgument("-c");
            ffmpeg.addArgument("copy");
            ffmpeg.addArgument("-map");
            ffmpeg.addArgument("0");
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("segment");
            ffmpeg.addArgument("-segment_list");
            ffmpeg.addArgument(m3u8FilePath);
            // 设置每个切片的时长
            ffmpeg.addArgument("-segment_time");
            ffmpeg.addArgument("3");
            ffmpeg.addArgument(tsFinalFilePath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.info(line);
                }
            }

            /**
             * 删除过程中保存的mp4、ts文件
             */
            new File(filePath).delete();
            new File(tsFilePath).delete();

        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        return gridFsId;
    }


    /**
     * @MethodName sliceProcess
     * @Description 上传视频文件、转换格式成 mp4、并进行切片
     * @Author travis-wei
     * @Data 2023/4/23
     * @param multipartFile
     * @Return java.lang.String 返回视频文件的 gridFsId 以供判断请求是否成功
     **/
    @PostMapping("/handle2")
    public String sliceProcessTwo(@RequestParam("file") MultipartFile multipartFile) {
        // 获取文件的名称（gridFsId + 文件后缀）
        String originalFilename = multipartFile.getOriginalFilename();
        if (StrUtil.isEmpty(originalFilename)) {
            return null;
        }
        // 获取文件的Context-type
        String contentType = multipartFile.getContentType();
        // 获取视频文件的gridFsId
        String gridFsId = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        // 获取视频文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        // 创建文件夹（文件路径中已经包含了gridFsId）
        String dirPath = createDir(gridFsId);


        // 临时存储的文件路径
        String tempPath = videoFilePath + originalFilename;
        // 最终mp4存储的文件路径
        String filePath = dirPath + gridFsId + ".mp4";
        // ts文件路径
        String tsFilePath = dirPath + gridFsId + ".ts";
        // m3u8视频索引路径
        String m3u8FilePath = dirPath + gridFsId + ".m3u8";
        // 最终视频切片文件存储的路径
        String tsFinalFilePath = dirPath + gridFsId + "-%05d.ts";
        // 通过 nginx 访问切片的路径
        String nginxBaseUrl = ffmpegNetUrl + gridFsId + "/";


        /**
         * 一、保存视频文件
         */
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = multipartFile.getInputStream();
            fileOutputStream = new FileOutputStream(tempPath);
            IOUtils.copy(inputStream, fileOutputStream);
            fileOutputStream.flush();

        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                return null;
            }
        }

        /**
         * 二、判断视频文件是否为mp4的格式，不是则转为mp4
         */
        File tempFile = new File(tempPath);
        if (suffix.equalsIgnoreCase("mp4")) {
            // 不需要转换文件格式，只需要移动文件
            tempFile.renameTo(new File(filePath));
        } else {
            boolean formatResult = formatToMp4(tempPath, filePath);
            if (formatResult) tempFile.delete();
        }

        /**
         * 三、进行切片操作（m3u8）
         */
        try {
            // 将mp4格式的文件转换为m3u8格式
            FFMPEGProcess ffmpeg = new FFMPEGProcess(ffmpegPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(filePath);
            // 指定视频解码器
            ffmpeg.addArgument("-c:v");
            ffmpeg.addArgument("libx264");
            // aac 音频解码器
            ffmpeg.addArgument("-c:a");
            ffmpeg.addArgument("aac");
            ffmpeg.addArgument("-strict");
            ffmpeg.addArgument("-2");
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("hls");
            // 设置 ts文件的网络路径信息（Nginx 路径）
            ffmpeg.addArgument("-hls_base_url");
            ffmpeg.addArgument(nginxBaseUrl);
            // 设置播放列表保存的最多条目，设置为 0 时会保存所有条目
            ffmpeg.addArgument("-hls_list_size");
            ffmpeg.addArgument("0");
            // 设置每个切片的时间长度，单位 为秒
            ffmpeg.addArgument("-hls_time");
            ffmpeg.addArgument("3");
            ffmpeg.addArgument(m3u8FilePath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.info(line);
                }
            }

            /**
             * 删除过程中保存的mp4文件
             */
            new File(filePath).delete();

        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }

        return gridFsId;
    }


    private boolean formatToMp4(String tempPath, String targetPath) {
        try {
            FFMPEGProcess ffmpeg = new FFMPEGProcess(ffmpegPath);
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(tempPath);
            ffmpeg.addArgument("-y");
            ffmpeg.addArgument("-c:v");
            ffmpeg.addArgument("libx264");
            ffmpeg.addArgument("-strict");
            ffmpeg.addArgument("-2");
            ffmpeg.addArgument(targetPath);
            ffmpeg.execute();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    log.info(line);
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * @MethodName createDir
     * @Description 根据GridFsId创建文件夹
     * @Author travis-wei
     * @Data 2023/4/21
     * @param gridFsId
     * @Return String
     **/
    private String createDir(String gridFsId) {
        String targetPath = videoFilePath + gridFsId + "/";
        File targetDir = new File(targetPath);

        // 如果文件夹不存在，创建文件夹
        if (!targetDir.exists()) {
            targetDir.mkdir();
            log.info("创建文件夹：{}", targetPath);
        }

        return targetPath;
    }


}
