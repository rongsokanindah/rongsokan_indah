package rongsokan.indah.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import rongsokan.indah.constants.TextConstant;

@Configuration
public class OpenAPIConfig {

    @Autowired
    private TextConstant textConstant;

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(
            new Info().title(textConstant.getTitle()).description(textConstant.getDescription())
                .contact(new Contact().name(textConstant.getEmail()).email(textConstant.getEmail()))
        );
    }
}