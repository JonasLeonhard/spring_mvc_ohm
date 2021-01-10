package controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pojos.ChallengeUploadForm
import services.ChallengeService
import services.UserService
import validators.FileValidator
import java.security.Principal
import javax.validation.Valid

@Controller
@RequestMapping("/challenge")
class ChallengeController(val userService: UserService, val challengeService: ChallengeService) {
    @GetMapping("/{id}")
    fun challengeContribution(principal: Principal, @PathVariable("id") challengeId: Long, model: Model): String {
        model["pageTitle"] = "Your Challenge Contribution"
        model["authenticated"] = userService.findByUsername(principal.name)

        val existingUserChallenge = challengeService.userChallenge(principal, challengeId)

        if (existingUserChallenge != null) {
            model["challengeUploadForm"] = ChallengeUploadForm(experience = existingUserChallenge.experience)
            model["existingUploadedFile"] = existingUserChallenge.image
        } else {
            model["challengeUploadForm"] = ChallengeUploadForm()
        }
        model["challengeId"] = challengeId

        return "challengeUpload"
    }

    @PostMapping("/{id}")
    fun challengeUpload(principal: Principal, @PathVariable("id") challengeId: Long, @RequestParam formFile: MultipartFile?, @Valid challengeUploadForm: ChallengeUploadForm, bindingResult: BindingResult, model: Model): String {

        FileValidator.fileMimeTypeValid(bindingResult, formFile, "file")
        challengeUploadForm.file = formFile

        println("challengeForm $challengeUploadForm bindingResult: $bindingResult")
        if (bindingResult.hasErrors()) {
            model["pageTitle"] = "Your Challenge Contribution"
            model["authenticated"] = userService.findByUsername(principal.name)

            model["challengeUploadForm"] = challengeUploadForm
            model["challengeId"] = challengeId
            return "challengeUpload"
        }

        val challenge = challengeService.challengeUpload(principal, challengeUploadForm, challengeId)
        return "redirect:/recipe/${challenge.recipe.id}"
    }

//    fun createChallenge(principal: Principal, model: Model): String {
//        // return create a challenge page for any given recipe
//        return "createChallenge"
//    }
}