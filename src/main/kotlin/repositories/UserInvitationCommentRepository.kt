package repositories

import models.UserInvitationComment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserInvitationCommentRepository : JpaRepository<UserInvitationComment, Long> {
    @Query("FROM UserInvitationComment comment WHERE comment.invitation.id = :invitationId")
    fun findAllCommentsForInvitation(@Param("invitationId") invitationId: Long): MutableList<UserInvitationComment>
}