package configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("properties")
@ConstructorBinding
data class ApplicationPropertiesConfiguration(
        val ENV_SPOONACULAR_API_KEY: String
)
