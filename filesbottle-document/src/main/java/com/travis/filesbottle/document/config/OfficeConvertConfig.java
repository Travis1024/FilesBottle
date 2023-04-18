package com.travis.filesbottle.document.config;

import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName OfficeConvertConfig
 * @Description kodconverter配置类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/4/17
 */
@Configuration
public class OfficeConvertConfig {

    @Value("${office.converter.office-home}")
    private String officeHome;

    @Value("${office.converter.port-numbers}")
    private Integer portNumbers;

    @Value("${office.converter.max-tasks-per-process}")
    private Integer maxTasksPerProcess;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public OfficeManager officeManager() {
        return LocalOfficeManager.builder()
                .officeHome(officeHome)
                .portNumbers(portNumbers)
                .maxTasksPerProcess(maxTasksPerProcess)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({OfficeManager.class})
    public DocumentConverter documentConverter(OfficeManager officeManager) {
        return LocalConverter.make(officeManager);
    }



}
