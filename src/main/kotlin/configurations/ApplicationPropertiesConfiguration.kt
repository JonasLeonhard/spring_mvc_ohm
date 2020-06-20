package configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import javax.annotation.PostConstruct

@ConfigurationProperties("properties")
@ConstructorBinding
data class ApplicationPropertiesConfiguration(
        var ENV_SPOONACULAR_API_KEY: String,
        val ANSI_RESET: String = "\u001B[0m",
        val ANSI_RED: String = "\u001B[31m"
) {
        @PostConstruct
        private fun onInit() {
                if (this.ENV_SPOONACULAR_API_KEY.isEmpty()) {
                        println("${this.ANSI_RED}Validation Error: \n ---ApplicationPropertiesConfiguration--- \nAPI KEY (ENV_SPOONACULAR_API_KEY) in Application.properties is NOT SET!,\n set it as an environment variable! (export ENV_SPOONACULAR_API_KEY=... for linux and macOs, or set ENV_SPOONACULAR_API_KEY=... for windows), \nRESTART THE APPLICATION AND ADD AN API KEY! ${this.ANSI_RESET}")
                } else {
                        println("INFO 00003 --- [ ENV_SPOONACULAR_API_KEY is set to: ${this.ENV_SPOONACULAR_API_KEY} ]")
                }
        }
}
