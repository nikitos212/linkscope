package com.example.linkscope.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI linkScopeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("LinkScope API")
                        .description("REST API for URL shortening and click analytics")
                        .version("1.0.0"));
    }
}
