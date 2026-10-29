package com.ruoyi.binZhou.ld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 配置类
 *
 * @author ld
 */
// @Configuration
public class RestTemplateConfig
{
    // @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }
}
