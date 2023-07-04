package com.travis.filesbottle.ffmpeg.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.process.ffmpeg.FFMPEGProcess;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Objects;

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
    @Value("${custom.ip}")
    private String customIp;
    @Value("${server.port}")
    private String port;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${ffmpeg.keypath}")
    private String keyPath;
    @Value("${ffmpeg.getkeyapi}")
    private String getKetApi;


    /**
     * @MethodName getVideo
     * @Description 在线预览视频信息
     * @Author travis-wei
     * @Data 2023/4/24
     * @param sourceId
     * @param response
     * @Return void
     **/
    @GetMapping("/video")
    public void getVideo(@RequestParam("sourceId") String sourceId, @RequestParam("token") String token, HttpServletResponse response) throws IOException {
        // TODO （更改 m3u8 文件）将 token 拼接到 m3u8 uri 后，web 在接收到 m3u8 后携带 token 去给定的 uri 获取密钥
        String filePath = videoFilePath + sourceId + "/" + sourceId + ".m3u8";
        FileReader fileReader = new FileReader(filePath);

        BufferedReader bufIn = new BufferedReader(fileReader.getReader());
        // 内存流, 作为临时流
        CharArrayWriter tempStream = new CharArrayWriter();
        // 替换
        String line = null;
        while ( (line = bufIn.readLine()) != null) {
            // 替换每行中, 符合条件的字符串
            line = line.replaceAll("&token=", "&token=" + token);
            // 将该行写入内存
            tempStream.write(line);
            // 添加换行符
            tempStream.append(System.getProperty("line.separator"));
        }
        // 关闭 输入流
        bufIn.close();

        String tempStreamString = tempStream.toString();
        log.info("tempStreamString:" + tempStreamString);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(tempStreamString.getBytes());
        IoUtil.copy(byteArrayInputStream, response.getOutputStream());
    }

    @GetMapping("/ts")
    public void getTsFile(@RequestParam("sourceId") String sourceId, @RequestParam("tsName") String tsName, HttpServletResponse response) throws IOException {
        String tsFilePath = videoFilePath + sourceId + "/" + tsName;
        FileReader fileReader = new FileReader(tsFilePath);
        log.info(tsFilePath);
        fileReader.writeToStream(response.getOutputStream());
    }


    /**
     * @MethodName getHandleUrl
     * @Description 获取上传视频文件的url 地址
     * @Author travis-wei
     * @Data 2023/4/24
     * @param
     * @Return java.lang.String
     **/
    @GetMapping("/gethandleurl")
    public String getHandleUrl() {
        return "http://" + customIp + ":" + port + contextPath + "/hlsvideo/handle";
    }


    /**
     * @MethodName sliceProcess
     * @Description 上传视频文件、转换格式成 mp4、并进行切片
     * 访问路径：https://140.246.171.8:48086/hlsvideo/handle (post请求)
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
        String dirPath = createDir(gridFsId);

        // 创建视频加密 所对应的 key 文件
        // 密钥文件路径
        String keyFilePath = keyPath + gridFsId + ".key";
        String keyFileFfmpegPath = keyPath + gridFsId + ".keyinfo";

        // 密钥 字符串
        String secretKey = IdUtil.simpleUUID();
        String iv = IdUtil.simpleUUID();
        byte[] secretKeyByte = secretKey.getBytes();
        File fileFfmpeg;
        try {
            File file = new File(keyFilePath);
            fileFfmpeg = new File(keyFileFfmpegPath);
            // 如果密钥文件不存在，则创建新文件
            if (!file.exists()) {
                file.createNewFile();
                fileFfmpeg.createNewFile();
            } else {
                throw new RuntimeException("密钥文件已经存在！");
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(secretKeyByte);
            outStream.close();
        } catch (Exception e) {
            log.error(e.toString());
            return e.getMessage();
        }
        // TODO 将密钥字符串存入数据库

        // TODO 修改 keyinfo 文件
        String keyInfoContent = getKetApi + gridFsId + "&token=" + "\n" + keyFilePath + "\n" + iv;
        byte[] keyInfoContentByte = keyInfoContent.getBytes();
        try {
            FileOutputStream outputStream = new FileOutputStream(fileFfmpeg);
            outputStream.write(keyInfoContentByte);
            outputStream.close();
        } catch (Exception e) {
            log.error(e.toString());
            return e.getMessage();
        }


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
            ffmpeg.addArgument("2");
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


    /**
     * @MethodName formatToMp4
     * @Description 将其他格式的视频格式转换为 mp4 的视频格式
     * @Author travis-wei
     * @Data 2023/4/24
     * @param tempPath
     * @param targetPath
     * @Return boolean
     **/
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

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            log.info("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFileByName(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else if (files[i].isDirectory()) {
                // 删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!dirFile.delete() || !flag) {
            log.info("删除目录失败！");
            return false;
        }
        return true;
    }


    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFileByName(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                log.info("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                log.info("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            log.info("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }
}
