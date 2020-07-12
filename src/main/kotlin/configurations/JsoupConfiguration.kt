package configurations

import org.jsoup.safety.Whitelist
import org.springframework.context.annotation.Configuration

@Configuration
data class JsoupConfiguration(
        val whiteList: Whitelist = Whitelist.none().addTags("br")
)