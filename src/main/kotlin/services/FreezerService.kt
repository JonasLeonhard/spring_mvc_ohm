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

    fun getFreezer(user: User, friends: MutableList<String>?): MutableList<Freezer> {
        val users = createUserAndFriendsList(user, friends)

        val freezers = mutableListOf<Freezer>()
        users.forEach { freezerUser ->
            val freezer = freezerRepository.findByUserId(freezerUser.id)
            if (freezer.isPresent) {
                freezers.addAll(freezer.get())
            }
        }

        return freezers
    }

    fun getSuggestions(user: User, friends: MutableList<String>?): MutableList<RecipeSuggestions> {
        val users = createUserAndFriendsList(user, friends)
        // gets the freezer for each user and add up all the freezer ingredients to a list
        val ingredients: MutableList<Ingredient> = mutableListOf()
        users.forEach { freezerUser ->
            val freezerOptional = freezerRepository.findByUserId(freezerUser.id)
            if (freezerOptional.isPresent) {
                val freezer = freezerOptional.get()
                val userIngredients = freezer.map { row ->
                    row.ingredient
                }.toMutableList()
                ingredients.addAll(userIngredients)
            }
        }
        return freezerRepository.findSuggestions(ingredients)
    }

    /**
     * find users for friends usernames & add them to users list if a friendship exists and is accepted
     * @return list of existing friendship users between user and friends list where friendship.accepted
     * */
    fun createUserAndFriendsList(user: User, friends: MutableList<String>?): MutableList<User> {
        val users = mutableListOf(user)

        friends?.forEach { username ->
            try {
                val friend = userService.findByUsername(username)
                val friendship = userService.findFriendshipBetween(user, friend)
                if (friendship != null && friendship.accepted) {
                    users.add(friend)
                }
            } catch (e: Exception) {
            }
        }
        return users
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