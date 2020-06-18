package configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@ConfigurationProperties("properties")
@ConstructorBinding
data class ApplicationPropertiesConfiguration(
        var ENV_SPOONACULAR_API_KEY: String,
        val ANSI_RESET: String = "\u001B[0m",
        val ANSI_RED: String = "\u001B[31m"
) {
        @PostConstruct
        fun throwApiKeyNotSetExceptionAfterCreation() {
                if (this.ENV_SPOONACULAR_API_KEY.isEmpty()) {
                        println("${this.ANSI_RED}Validation Error: ---ApplicationPropertiesConfiguration--- API KEY (ENV_SPOONACULAR_API_KEY) in Application.properties is NOT SET!, set it as an environment variable (export ENV_SPOONACULAR_API_KEY=... for linux and macOs, or set ENV_SPOONACULAR_API_KEY=... for windows) ${this.ANSI_RESET}")
                        exitProcess(404)
                }
        }
}
