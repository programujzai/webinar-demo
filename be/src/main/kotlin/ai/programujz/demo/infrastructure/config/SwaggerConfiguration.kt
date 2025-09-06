package ai.programujz.demo.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Todo List API")
                    .description("API documentation for Todo List application")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Development Team")
                            .email("dev@example.com")
                    )
            )
    }
}