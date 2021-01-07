package repositories

import models.Invitation
import models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface InvitationRepository : JpaRepository<Invitation, Long> {
    @Query("""
        SELECT invitation FROM Invitation invitation
        WHERE invitation.user = :user
        OR :user MEMBER OF invitation.friends
        AND invitation.date >= :startDate
        AND invitation.date <= :endDate
        ORDER BY invitation.date ASC
    """)
    fun findInvitationsBetween(@Param("user") user: User,
                               @Param("startDate") startDate: Date,
                               @Param("endDate") endDate: Date): MutableList<Invitation>
}