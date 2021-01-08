package repositories

import models.UserChallenge
import org.springframework.data.jpa.repository.JpaRepository

interface UserChallengeRepository : JpaRepository<UserChallenge, Long> {
}