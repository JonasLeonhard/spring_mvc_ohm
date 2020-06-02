package repositories

import models.Friendship
import models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface FriendshipRepository : JpaRepository<Friendship, Long> {
    @Modifying
    @Query("UPDATE Friendship friendship SET friendship.accepted = ?1 WHERE friendship.id = ?2")
    fun updateFriendshipAccepted(accepted: Boolean, userId: Long)

    @Query("SELECT friendship FROM Friendship friendship where friendship.requested_by = ?1 and friendship.request_to = ?2")
    fun findFriendshipBetween(from_user: User, to_user: User): Friendship?
}