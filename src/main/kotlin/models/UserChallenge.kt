package models

import java.io.Serializable
import javax.persistence.*

@Entity
class UserChallenge(
        user: User,
        challenge: Challenge
) {
    var user: User
        get() = this.embeddedKey.user
        set(value) {
            this.embeddedKey.user = value
        }

    var challenge: Challenge
        get() = this.embeddedKey.challenge
        set(value) {
            this.embeddedKey.challenge = value
        }

    @EmbeddedId
    private var embeddedKey: UserChallengeCompositeKey = UserChallengeCompositeKey(user, challenge)
}

@Embeddable
data class UserChallengeCompositeKey(
        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User,

        @ManyToOne
        @JoinColumn(name = "challenge_id")
        var challenge: Challenge
) : Serializable

