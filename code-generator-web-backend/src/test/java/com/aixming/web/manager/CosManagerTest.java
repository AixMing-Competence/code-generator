package com.aixming.web.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author AixMing
 * @since 2024-10-05 21:10:56
 */
@SpringBootTest
class CosManagerTest {
    
    @Resource
    private CosManager cosManager;

    @Test
    void deleteObject() {
        cosManager.deleteObject("/generator_picture/1/ZXOgoQeN-test1.jpg");
    }

    @Test
    void deleteObjects() {
        cosManager.deleteObjects(Arrays.asList("generator_make_template/1/c20PWqzu-acm-template-pro.zip","generator_make_template/1/pMQftdkc-acm-template-pro.zip"));
    }

    @Test
    void deleteDir() {
        cosManager.deleteDir("/test/");
    }
}