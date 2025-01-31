package pt.rs

import kotlinx.datetime.Clock
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
