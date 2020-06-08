package services

import models.Friendship
import models.Notification
import models.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import repositories.FriendshipRepository
import repositories.NotificationRepository
import repositories.RoleRepository
import repositories.UserRepository
import javax.management.relation.RoleNotFoundException

@Service
class UserService(
        val userRepository: UserRepository,
        val roleRepository: RoleRepository,
        val friendshipRepository: FriendshipRepository,
        val notificationRepository: NotificationRepository,
        val bCryptPasswordEncoder: BCryptPasswordEncoder,
        val fileService: FileService) {

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
}

class FriendshipAlreadyExistException(message: String) : Exception(message)
class SelfFriendRequestException(message: String) : Exception(message)
class NotificationAlreadyExistsException(notification: Notification) : Exception("notification with id ${notification.id} already exists")
