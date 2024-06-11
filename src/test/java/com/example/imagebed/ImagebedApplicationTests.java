package com.example.imagebed;

import imagebed.ImagebedApplication;
import imagebed.service.ConfigService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ImagebedApplication.class)
class ImagebedApplicationTests {


    @Resource
    private ConfigService configService;

    @Test
    public void contextLoads() {
        System.out.println("test test");
    }

    @Test
    public void testConfig() {
        System.out.println("配置信息"+configService.getConfig());

    }

}
