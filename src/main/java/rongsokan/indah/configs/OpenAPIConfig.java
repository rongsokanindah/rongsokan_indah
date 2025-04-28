package rongsokan.indah.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import rongsokan.indah.constants.TextConstant;

@Configuration
public class OpenAPIConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(
            new Info().title(TextConstant.TITLE).description(TextConstant.DESCRIPTION)
                .contact(new Contact().name(TextConstant.EMAIL).email(TextConstant.EMAIL))
        );
    }
}