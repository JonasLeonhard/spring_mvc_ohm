package services

import models.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import repositories.RoleRepository
import repositories.UserRepository
import javax.management.relation.RoleNotFoundException

@Service
class UserService(val userRepository: UserRepository, val roleRepository: RoleRepository, val bCryptPasswordEncoder: BCryptPasswordEncoder) {
    fun findByUserName(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun registerNewUser(user: User) {
        user.password = bCryptPasswordEncoder.encode(user.password)

        val role = roleRepository.findByName("User")
                ?: throw RoleNotFoundException("UserService.save: 'User' role is undefined")

        user.roles.add(role)
        userRepository.save(user)
    }
}
