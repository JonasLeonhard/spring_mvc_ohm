package configurations

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import services.ChallengeService


@Configuration
@EnableAsync
@EnableScheduling
class ChronjobConfiguration(val challengeService: ChallengeService) {
    @Scheduled(cron = "0 0 4 * * *") // 4AM every Day
    fun dailyChronjob() {
        challengeService.changeChallenge()
    }
}