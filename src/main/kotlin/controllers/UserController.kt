package controllers

import models.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pojos.AddIngredientForm
import pojos.InvitationForm
import services.FreezerService
import services.InvitationService
import services.UserService
import validators.AddIngredientFormValidator
import validators.InvitationFormValidator
import java.security.Principal
import javax.validation.Valid

@Controller
@RequestMapping("/user")
class UserController(val userService: UserService,
                     val freezerService: FreezerService,
                     val invitationService: InvitationService,
                     val addIngredientFormValidator: AddIngredientFormValidator,
                     val invitationFormValidator: InvitationFormValidator) {

    @GetMapping("/profile/{username}")
    fun userProfile(principal: Principal?, model: Model, @PathVariable username: String): String {
        model["userForm"] = User()
        try {
            val profile = userService.findByUsername(username)
            model["profile"] = profile

            if (principal != null) {
                val authenticated = userService.findByUsername(principal.name)
                model["authenticated"] = authenticated
                model["userFriendships"] = userService.getFriendships(authenticated)

                if (authenticated != profile) {
                    val friendship = userService.findFriendshipBetween(authenticated, profile)
                    if (friendship != null) model["friendship"] = friendship
                }
            }

        } catch (userNotFound: Exception) {
            model["userNotFound"] = username
        }

        model["pageTitle"] = "${username}'s Profile"
        return "profile"
    }

    @GetMapping("/settings")
    fun userSettings(principal: Principal, model: Model): String {
        userService.addAuthenticatedUserToModel(principal, model)
        model["pageTitle"] = "${principal.name}' Settings"
        return "settings"
    }

    @GetMapping("/buyList")
    fun userBuyList(principal: Principal, model: Model): String {
        userService.addAuthenticatedUserToModel(principal, model)
        return "buyList"
    }

    @GetMapping("/freezer")
    fun freezer(principal: Principal, model: Model, @RequestParam(value = "friends") friends: MutableList<String>?): String {
        val user = userService.findByUsername(principal.name)
        model["authenticated"] = user
        model["freezer"] = freezerService.getFreezer(user, friends)
        model["ingredients"] = freezerService.getIngredients()
        model["freezerSuggestions"] = freezerService.getSuggestions(user, friends)
        model["userFriendships"] = userService.getFriendships(user)
        if (friends != null) {
            model["queryFriends"] = friends
        }

        return "freezer"
    }

    @GetMapping("/freezer/add/ingredient")
    fun addIngredient(principal: Principal, @RequestParam ingredientName: String, @RequestParam amount: Int, @RequestParam friends: MutableList<String>?, model: Model): String {
        userService.addAuthenticatedUserToModel(principal, model)
        model["addIngredientForm"] = AddIngredientForm(name = ingredientName)
        model["amount"] = amount
        if (friends != null) {
            model["queryFriends"] = friends
        }
        return "addIngredient"
    }

    @GetMapping("/invite")
    fun inviteFriends(principal: Principal, invitationForm: InvitationForm, @RequestParam invitationId: Long?, model: Model): String {
        val user = userService.findByUsername(principal.name)
        model["authenticated"] = user
        model["userFriendships"] = userService.getFriendships(user)

        if (invitationId != null) {
            model["invitationId"] = invitationId
            model["invitationForm"] = invitationService.getUserInvitationFormByInvitationId(user, invitationId)
        } else {
            model["invitationForm"] = invitationForm
        }
        return "invite"
    }

    @GetMapping("/invitation/{id}")
    fun invitation(principal: Principal, @PathVariable(value = "id") invitationId: Long, model: Model): String {
        val user = userService.findByUsername(principal.name)
        model["authenticated"] = user
        val invitation = invitationService.getInvitationById(invitationId).get()

        if (!invitation.containsUser(user)) {
            throw Exception("InvitationException: invitation does not contain user")
        }

        model["invitation"] = invitation

        model["invitationComments"] = invitationService.getInvitationComments(invitationId)
        return "invitation"
    }

    @PostMapping("/friendship")
    fun friendship(principal: Principal, @RequestParam(value = "userId", required = true) userId: Long, model: Model): String {
        val authenticated = userService.findByUsername(principal.name)
        val toUser = userService.findById(userId)

        model["authenticated"] = authenticated
        model["pageTitle"] = "${principal.name}' Settings"
        model["friendship"] = userService.friendRequest(authenticated, toUser)
        return "redirect:/user/profile/${toUser.username}"
    }

    @PostMapping("/friendship/accept")
    fun friendshipAccept(principal: Principal, @RequestParam(value = "userId", required = true) userId: Long, model: Model): String {
        val authenticated = userService.findByUsername(principal.name)
        val toUser = userService.findById(userId)
        userService.friendRequestAccept(authenticated, toUser)
        return "redirect:/user/profile/${toUser.username}"
    }

    @PostMapping("/friendship/cancel")
    fun friendshipCancel(principal: Principal, @RequestParam(value = "userId", required = true) userId: Long, @RequestParam(value = "stayOnPage") stayOnPage: Boolean?, model: Model): String {
        val authenticated = userService.findByUsername(principal.name)
        val toUser = userService.findById(userId)
        userService.friendRequestCancel(authenticated, toUser)
        if (stayOnPage != null && stayOnPage) {
            return "redirect:/user/profile/${authenticated.username}"
        }
        return "redirect:/user/profile/${toUser.username}"
    }

    @PostMapping("/freezer")
    fun freezer(principal: Principal, @RequestParam(value = "ingredientName") ingredientName: String, @RequestParam(value = "amount") amount: Int, @RequestParam(value = "friends") friends: MutableList<String>?): String {
        val ingredientInFreezer = freezerService.addIngredientToFreezer(principal, ingredientName, amount)
        return if (!ingredientInFreezer) {
            "redirect:/user/freezer/add/ingredient?ingredientName=$ingredientName&amount=$amount${userService.getFriendQueryParams(friends, true)}"
        } else {
            "redirect:/user/freezer${userService.getFriendQueryParams(friends, false)}"
        }
    }

    @PostMapping("/freezer/update")
    fun changeFreezerItem(principal: Principal, @RequestParam freezerIngredientId: Long, @RequestParam subtract: Boolean?, @RequestParam delete: Boolean?, @RequestParam friends: MutableList<String>?): String {
        val user = userService.findByUsername(principal.name)

        if (delete != null && delete) {
            freezerService.deleteMapping(user, freezerIngredientId)
        } else {
            freezerService.incrementAmount(user, freezerIngredientId, subtract)
        }

        return "redirect:/user/freezer${userService.getFriendQueryParams(friends, false)}"
    }

    @PostMapping("/freezer/add/ingredient")
    fun addIngredient(principal: Principal, @Valid addIngredientForm: AddIngredientForm, bindingResult: BindingResult, @RequestParam formFile: MultipartFile?, @RequestParam amount: Int, @RequestParam friends: MutableList<String>?, model: Model): String {
        println("post /freezer/add/ingredient inAddIngredient $addIngredientForm")
        userService.addAuthenticatedUserToModel(principal, model)

        addIngredientForm.file = formFile
        addIngredientFormValidator.validate(addIngredientForm, bindingResult)
        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
            model["addIngredientForm"] = addIngredientForm
            model["amount"] = amount
            if (friends != null) {
                model["queryFriends"] = friends
            }
            return "addIngredient"
        }

        freezerService.addIngredientFormToFreezer(principal, addIngredientForm, amount)
        return "redirect:/user/freezer${userService.getFriendQueryParams(friends, false)}"
    }

    @PostMapping("/invite")
    fun invitation(principal: Principal, @Valid invitationForm: InvitationForm, bindingResult: BindingResult, @RequestParam(name = "invitationId") invitationId: Long?, model: Model): String {
        println("POST: /invite -> invitationId: $invitationId")
        val user = userService.findByUsername(principal.name)

        if (invitationId != null) {
            // Edit the invitation based on invitationForm?
            return "redirect:invitation/${invitationId}"
        }

        invitationFormValidator.validate(principal, invitationForm, bindingResult)
        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
            model["userFriendships"] = userService.getFriendships(user)
            model["invitationForm"] = invitationForm
            model["authenticated"] = user
            return "invite"
        }
        val invitation = invitationService.createInvitation(user, invitationForm)
        return "redirect:invitation/${invitation.id}"
    }

    @PostMapping("/invitation/{id}/comment")
    fun commentOn(principal: Principal, @PathVariable("id") invitationId: Long, @RequestParam(name = "message") message: String): String {
        userService.commentInvitation(invitationId, principal, message)
        return "redirect:/user/invitation/$invitationId"
    }
}