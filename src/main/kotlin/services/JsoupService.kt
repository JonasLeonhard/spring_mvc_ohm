package services

import configurations.JsoupConfiguration
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class JsoupService(val jsoupConfig: JsoupConfiguration) {
    fun escapeUserText(unsafe: String): String {
        return Jsoup.clean(unsafe, jsoupConfig.whiteList)
    }
}