package services

import models.Friendship
import models.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import repositories.FriendshipRepository
import repositories.RoleRepository
import repositories.UserRepository
import javax.management.relation.RoleNotFoundException

@Service
class UserService(
        val userRepository: UserRepository,
        val roleRepository: RoleRepository,
        val friendshipRepository: FriendshipRepository,
        val bCryptPasswordEncoder: BCryptPasswordEncoder,
        val fileService: FileService) {

    @Throws(UsernameNotFoundException::class)
    fun findByUsername(username: String): User {
        return userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
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
    fun friendRequest(from: User, to: User): Friendship {
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
}
