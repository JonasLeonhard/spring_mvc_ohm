package services

import models.*
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
        val userRecipeBuyListRepository: UserRecipeBuyListRepository,
        val userRecipeCommentRepository: UserRecipeCommentRepository,
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
    fun friendRequestAccept(authenticated: User, profile: User): Boolean {
        val friendship = friendshipRepository.findFriendshipBetween(authenticated, profile)
        if (friendship != null && friendship.request_to.id == authenticated.id) {
            friendship.accepted = true
            friendshipRepository.save(friendship)
            return true
        }
        return false
    }

    @Transactional
    fun friendRequestCancel(from: User, to: User): Boolean {
        val friendship = friendshipRepository.findFriendshipBetween(from, to)
        if (friendship != null) {
            friendshipRepository.delete(friendship)
            return true
        }
        return false
    }

    fun findFriendshipBetween(from: User, to: User): Friendship? {
        return friendshipRepository.findFriendshipBetween(from, to)
    }

    /**
     * Get all friendships of a given user
     * @return List of accepted Friendships
     * */
    fun getFriendships(user: User): List<Friendship> {
        val friendships = mutableSetOf<Friendship>()
        friendships.addAll(user.friendshipsToThisUser)
        friendships.addAll(user.friendshipsFromThisUser)

        return friendships.filter { friendship ->
            friendship.accepted
        }
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

    /**
     * Favorite a Recipe by a given User
     * @return Boolean if the recipe is a favorite of the user
     * */
    @Transactional
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

    /**
     * Adds / remove a Recipe to / from the users buy list
     * */
    @Transactional
    fun addRecipeToBuyList(recipeId: Long, principal: Principal) {
        val recipe = recipeRepository.findById(recipeId).get()
        val user = findByUsername(principal.name)
        val newUserRecipeBuyList = UserRecipeBuyList(user, recipe)

        user.buyList.forEach { userRecipeBuyList ->
            if (userRecipeBuyList.isSameRow(newUserRecipeBuyList)) {
                userRecipeBuyListRepository.delete(userRecipeBuyList)
                return
            }
        }

        userRecipeBuyListRepository.save(newUserRecipeBuyList)
    }

    @Transactional
    fun commentRecipe(recipeId: Long, principal: Principal, message: String) {
        val recipe = recipeRepository.findById(recipeId).get()
        val user = findByUsername(principal.name)

        userRecipeCommentRepository.save(UserRecipeComment(
                user = user,
                recipe = recipe,
                message = message
        ))
    }

    fun getFriendQueryParams(friends: MutableList<String>?, queryParamsExist: Boolean): String {
        var queryParams = ""
        friends?.forEachIndexed { index, friend ->
            if (index == 0 && !queryParamsExist) {
                queryParams += "?"
            } else if (index == 0 && queryParamsExist || index != 0) {
                queryParams += "&"
            }
            queryParams += "friends=$friend"
        }
        return queryParams
    }
}

class FriendshipAlreadyExistException(message: String) : Exception(message)
class SelfFriendRequestException(message: String) : Exception(message)
class NotificationAlreadyExistsException(notification: Notification) : Exception("notification with id ${notification.id} already exists")
