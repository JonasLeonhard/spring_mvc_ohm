package services

import models.Friendship
import models.Notification
import models.User
import models.UserLikedRecipe
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.ui.set
import repositories.*
import java.security.Principal
import javax.management.relation.RoleNotFoundException

@Service
class UserService(
        val userRepository: UserRepository,
        val roleRepository: RoleRepository,
        val recipeRepository: RecipeRepository,
        val userLikedRecipeRepository: UserLikedRecipeRepository,
        val friendshipRepository: FriendshipRepository,
        val notificationRepository: NotificationRepository,
        val bCryptPasswordEncoder: BCryptPasswordEncoder,
        val fileService: FileService) {

    /**
     * Add model["authenticated"] if the principal user is not null.
     * The authenticated user refers to userService.findByUsername
     * @return <User?> for the given principal
     * */
    fun addAuthenticatedUserToModel(principal: Principal?, model: Model): User? {
        return if (principal != null) {
            val user = findByUsername(principal.name)
            model["authenticated"] = user
            user
        } else {
            null
        }
    }

    @Throws(UsernameNotFoundException::class)
    fun findByUsername(username: String): User {
        return userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
    }

    fun findAllByUsername(username: String): Set<User> {
        return userRepository.findAllByUsername(username)
    }

    fun searchByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun searchAllByUsername(username: String): Set<User> {
        return userRepository.searchAllByUsername(username)
    }

    @Throws(NoSuchElementException::class)
    fun findById(id: Long): User {
        return userRepository.findById(id).get()
    }

    @Transactional
    @Throws(RoleNotFoundException::class, IllegalArgumentException::class)
    fun registerNewUser(user: User): User {
        user.password = bCryptPasswordEncoder.encode(user.password)

        val role = roleRepository.findByName("User")
                ?: throw RoleNotFoundException("UserService.save: 'User' role is undefined")

        user.roles.add(role)
        user.picture = fileService.trySaveMultipartFile(user.file)

        return userRepository.save(user)
    }

    @Transactional
    @Throws(SelfFriendRequestException::class, FriendshipAlreadyExistException::class)
    fun friendRequest(from: User, to: User): Friendship {
        if (from.id == to.id)
            throw SelfFriendRequestException("Cannot Friendship Users with same id")
        if (friendshipRepository.findFriendshipBetween(from, to) != null)
            throw FriendshipAlreadyExistException("A Friendship between ${from.username} and ${to.username} already exists")

        val friendship = Friendship(requested_by = from, request_to = to)
        return friendshipRepository.save(friendship)
    }

    @Transactional
    fun friendRequestAccept(friendship: Friendship): Friendship {
        friendship.accepted = true
        return friendship
    }

    fun findFriendshipBetween(from: User, to: User): Friendship? {
        return friendshipRepository.findFriendshipBetween(from, to)
    }

    @Transactional
    @Throws(IllegalArgumentException::class)
    fun notifiyUser(notification: Notification): Notification {
        return notificationRepository.save(notification)
    }

    /**
     * Adds / removes a user to user_profile_liked_recipes table
     * If the user.likedRecipes already contains a liked relation of UserLikedRecipe, then the like row will be removed,
     * otherwise a new UserLikedRecipe row will be created
     * */
    @Transactional
    fun likeRecipe(recipeId: Long, principal: Principal) {
        val recipe = recipeRepository.findById(recipeId).get()
        val user = findByUsername(principal.name)
        val newUserLikedRecipe = UserLikedRecipe(user, recipe)

        user.likedRecipes.forEach { userLikedRecipe ->
            if (userLikedRecipe.isSameRow(newUserLikedRecipe)) {
                userLikedRecipeRepository.delete(userLikedRecipe)
                return
            }
        }

        userLikedRecipeRepository.save(newUserLikedRecipe)
    }

    @Transactional
            /**
             * Favorite a Recipe by a given User
             * @return Boolean if the recipe is a favorite of the user
             * */
    fun favoriteRecipe(recipeId: Long, principal: Principal): Boolean {
        val recipe = recipeRepository.findById(recipeId).get()
        val user = findByUsername(principal.name)

        val addedToFavorites = user.favoriteRecipes.add(recipe)
        if (!addedToFavorites) {
            user.favoriteRecipes.remove(recipe)
        }

        userRepository.save(user)
        return addedToFavorites
    }
}

class FriendshipAlreadyExistException(message: String) : Exception(message)
class SelfFriendRequestException(message: String) : Exception(message)
class NotificationAlreadyExistsException(notification: Notification) : Exception("notification with id ${notification.id} already exists")
