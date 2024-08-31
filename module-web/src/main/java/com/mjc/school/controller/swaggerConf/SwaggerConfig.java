package com.mjc.school.controller.swaggerConf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.builders.ApiInfoBuilder;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiAppInfo()).useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mjc.school.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(apiKey()))
                .securityContexts(List.of(securityContext()));
    }

    private ApiInfo apiAppInfo() {
        return new ApiInfoBuilder().title("News application REST API")
                .description("News  application REST API")
                .contact(new Contact("MJC School", "https://mjc.school/", "estorskaya@gmail.com"))
                .license("Apache 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .version("0.1")
                .build();

    }
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }

}
