package com.example.idtypedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.idtypedemo.version.config.VersionTrackingProperties;

@SpringBootApplication
@EnableConfigurationProperties(VersionTrackingProperties.class)
public class IdTypeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdTypeDemoApplication.class, args);
    }
} 