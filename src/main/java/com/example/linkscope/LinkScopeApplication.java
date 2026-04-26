package com.example.linkscope;

import com.example.linkscope.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class LinkScopeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkScopeApplication.class, args);
    }
}
