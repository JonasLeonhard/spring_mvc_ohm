package services

import models.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import repositories.RoleRepository
import repositories.UserRepository

@Service
class UserService(val userRepository: UserRepository, val roleRepository: RoleRepository, val bCryptPasswordEncoder: BCryptPasswordEncoder) {
    fun findByUserName(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun save(user: User) {
        user.password = bCryptPasswordEncoder.encode(user.password)

        // TODO() <- this should be a query by name = "USER"
        user.roles.add(roleRepository.getOne(2)) // id = 2 := "USER" Role

        userRepository.save(user)
    }
}
