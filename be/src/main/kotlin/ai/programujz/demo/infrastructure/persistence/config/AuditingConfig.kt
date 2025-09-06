package ai.programujz.demo.infrastructure.persistence.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import java.time.Instant
import java.util.Optional

/**
 * Configuration for Spring Data JDBC auditing.
 * Enables automatic population of createdAt and updatedAt fields.
 */
@Configuration
@EnableJdbcAuditing(dateTimeProviderRef = "instantDateTimeProvider")
class AuditingConfig {
    
    @Bean
    fun instantDateTimeProvider(): DateTimeProvider {
        return DateTimeProvider { Optional.of(Instant.now()) }
    }
}