package repositories

import models.Invitation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface InvitationRepository : JpaRepository<Invitation, Long> {
    @Query("SELECT invitation FROM Invitation invitation " +
            "WHERE invitation.user.id = :userId " +
            "AND invitation.date >= :startDate " +
            "AND invitation.date <= :endDate " +
            "ORDER BY invitation.date ASC")
    fun findInvitationsBetween(@Param("userId") userId: Long,
                               @Param("startDate") startDate: Date,
                               @Param("endDate") endDate: Date): MutableList<Invitation>
}