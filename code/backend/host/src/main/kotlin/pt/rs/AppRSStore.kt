package pt.rs

import kotlinx.datetime.Clock
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.rs.mem.TransactionManagerInMem
import pt.rs.user.Sha256TokenEncoder
import pt.rs.user.UsersDomainConfig
import kotlin.time.Duration.Companion.hours
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.OpenAPI
import org.springdoc.core.models.GroupedOpenApi

@Configuration
@Suppress("unused")
class PipelineConfig(
    private val authInterceptor: AuthenticationInterceptor,
    private val authArgumentResolver: AuthenticatedUserArgumentResolver
): WebMvcConfigurer{
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authArgumentResolver)
    }
}
@Suppress("unused")
@Configuration
class ApplicationConfig{
    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("base-service")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun customOpenAPI(
        @Value("\${application-description}") appDescription: String?, @Value(
            "\${application-version}"
        ) appVersion: String?
    ): OpenAPI {
        val contact = Contact()
        contact.email = " "
        contact.name = "Paulo Carvalho"
        return OpenAPI()
            .info(
                Info()
                    .title("Online Store API")
                    .version(appVersion)
                    .description(appDescription)
                    .termsOfService("http://swagger.io/terms/")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
                    .contact(contact)
            )
    }
}


@SpringBootApplication
@Suppress("unused")
class AppRSStore {

    @Bean
    fun trxManager():TransactionManagerInMem = TransactionManagerInMem()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock() = Clock.System

    @Bean
    fun usersDomainConfig() =
        UsersDomainConfig(
            tokenSizeInBytes = 256 / 8,
            tokenTtl = 24.hours,
            tokenRollingTtl = 1.hours,
            maxTokensPerUser = 3,
        )
}
fun main() {
    runApplication<AppRSStore>()
}
