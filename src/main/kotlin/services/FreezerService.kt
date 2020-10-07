package services

import models.Freezer
import models.Ingredient
import models.User
import org.springframework.data.domain.Sort
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pojos.AddIngredientForm
import pojos.RecipeSuggestions
import repositories.FreezerRepository
import repositories.IngredientRepository
import java.security.Principal

@Service
class FreezerService(val freezerRepository: FreezerRepository,
                     val ingredientRepository: IngredientRepository,
                     val userService: UserService,
                     val fileService: FileService) {

    @Throws(UsernameNotFoundException::class, IllegalArgumentException::class)
    @Transactional
    fun addIngredientToFreezer(principal: Principal, ingredientName: String, amount: Int): Boolean {
        println("add ingredient to freezer!")
        val user = userService.findByUsername(principal.name)
        val ingredient = ingredientRepository.findIngredientByName(ingredientName)


        if (ingredient != null) {
            val freezerOptional = freezerRepository.findById(user.id, ingredient.id)
            if (freezerOptional.isPresent) {
                val freezer = freezerOptional.get()
                freezer.amount = amount
                freezerRepository.save(freezer)
            } else {
                freezerRepository.save(Freezer(user = user, ingredient = ingredient, amount = amount))
            }
            return true
        }

        return false
    }

    @Throws(UsernameNotFoundException::class, IllegalArgumentException::class)
    @Transactional
    fun addIngredientFormToFreezer(principal: Principal, addIngredientForm: AddIngredientForm, amount: Int) {
        val ingredient = Ingredient(
                name = addIngredientForm.name!!,
                meta = addIngredientForm.meta,
                aisle = addIngredientForm.aisle,
                consistency = addIngredientForm.consistency,
                unit = addIngredientForm.unit!!)
        ingredient.picture = fileService.trySaveMultipartFile(addIngredientForm.file)

        val savedIngredient = ingredientRepository.save(ingredient)

        val user = userService.findByUsername(principal.name)
        freezerRepository.save(Freezer(user, savedIngredient, amount))
    }

    fun getIngredients(): MutableList<Ingredient> {
        val sort: Sort = Sort.by("name").ascending()
        return ingredientRepository.findAll(sort)
    }

    fun getFreezer(user: User): MutableList<Freezer> {
        return try {
            freezerRepository.findByUserId(user.id).get()
        } catch (e: NoSuchElementException) {
            mutableListOf()
        }
    }

    fun getSuggestions(user: User): MutableList<RecipeSuggestions> {
        val freezer = freezerRepository.findByUserId(user.id).get()
        val ingredients = freezer.map { row ->
            row.ingredient
        }.toMutableList()

        return freezerRepository.findSuggestions(ingredients)
    }

    @Throws(NoSuchElementException::class)
    @Transactional
    fun deleteMapping(user: User, ingredientId: Long) {
        val freezer = freezerRepository.findById(user.id, ingredientId).get()
        freezerRepository.delete(freezer)
    }

    @Transactional
    fun incrementAmount(user: User, ingredientId: Long, subtract: Boolean?) {
        val freezer = freezerRepository.findById(user.id, ingredientId).get()

        if (subtract != null && subtract) {
            if (freezer.amount > 0) {
                freezer.amount -= 1
            }
        } else {
            freezer.amount += 1
        }

        freezerRepository.save(freezer)
    }
}