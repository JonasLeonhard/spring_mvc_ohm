package controllers

import models.User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pojos.AddIngredientForm
import services.FreezerService
import services.UserService
import validators.AddIngredientFormValidator
import java.security.Principal
import javax.validation.Valid

@Controller
@RequestMapping("/user")
class UserController(val userService: UserService, val freezerService: FreezerService, val addIngredientFormValidator: AddIngredientFormValidator) {

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
        model["freezer"] = freezerService.getFreezer(user)
        model["ingredients"] = freezerService.getIngredients()
        model["freezerSuggestions"] = freezerService.getSuggestions(user, friends)
        model["userFriendships"] = userService.getFriendships(user)

        return "freezer"
    }

    @GetMapping("/freezer/add/ingredient")
    fun addIngredient(principal: Principal, @RequestParam ingredientName: String, @RequestParam amount: Int, model: Model): String {
        println("get /freezer/add/ingredient")
        userService.addAuthenticatedUserToModel(principal, model)
        model["addIngredientForm"] = AddIngredientForm(name = ingredientName)
        model["amount"] = amount

        return "addIngredient"
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
        val accept = userService.friendRequestAccept(authenticated, toUser)
        println("friendrequest accept: $accept")
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
    fun freezer(principal: Principal, @RequestParam(value = "ingredientName") ingredientName: String, @RequestParam(value = "amount") amount: Int): String {
        val ingredientInFreezer = freezerService.addIngredientToFreezer(principal, ingredientName, amount)
        return if (!ingredientInFreezer) {
            "redirect:/user/freezer/add/ingredient?ingredientName=$ingredientName&amount=$amount"
        } else {
            "redirect:/user/freezer"
        }
    }

    @PostMapping("/freezer/update")
    fun changeFreezerItem(principal: Principal, @RequestParam freezerIngredientId: Long, @RequestParam subtract: Boolean?, @RequestParam delete: Boolean?): String {
        val user = userService.findByUsername(principal.name)

        if (delete != null && delete) {
            freezerService.deleteMapping(user, freezerIngredientId)
        } else {
            freezerService.incrementAmount(user, freezerIngredientId, subtract)
        }

        return "redirect:/user/freezer"
    }

    @PostMapping("/freezer/add/ingredient")
    fun addIngredient(principal: Principal, @Valid addIngredientForm: AddIngredientForm, bindingResult: BindingResult, @RequestParam formFile: MultipartFile?, @RequestParam amount: Int, model: Model): String {
        println("post /freezer/add/ingredient inAddIngredient $addIngredientForm")
        userService.addAuthenticatedUserToModel(principal, model)

        addIngredientForm.file = formFile
        addIngredientFormValidator.validate(addIngredientForm, bindingResult)
        if (bindingResult.hasErrors()) {
            model["errors"] = bindingResult
            model["addIngredientForm"] = addIngredientForm
            model["amount"] = amount
            return "addIngredient"
        }

        freezerService.addIngredientFormToFreezer(principal, addIngredientForm, amount)
        return "redirect:/user/freezer"
    }
}