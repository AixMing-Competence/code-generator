package com.aixming.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 *
 * @author AixMing
 * @since 2024-10-04 15:38:23
 */
@RestController
@RequestMapping("/health")
public class HealthController {
    
    @GetMapping
    public String healthCheck() {
        return "ok";
    }
}
