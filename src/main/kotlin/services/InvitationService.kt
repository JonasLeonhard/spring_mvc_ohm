package services

import models.Invitation
import org.springframework.stereotype.Service
import pojos.InvitationForm
import java.security.Principal

@Service
class InvitationService {

    fun createInvitation(principal: Principal, invitationForm: InvitationForm): Invitation {
        TODO("CREATE INVITATION :::")
    }
}