package services

import models.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import repositories.RoleRepository
import repositories.UserRepository
import javax.management.relation.RoleNotFoundException

@Service
class UserService(
        val userRepository: UserRepository,
        val roleRepository: RoleRepository,
        val bCryptPasswordEncoder: BCryptPasswordEncoder,
        val fileService: FileService) {
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional
    fun registerNewUser(user: User): User {
        user.password = bCryptPasswordEncoder.encode(user.password)

        val role = roleRepository.findByName("User")
                ?: throw RoleNotFoundException("UserService.save: 'User' role is undefined")

        user.roles.add(role)
        val savedFile = fileService.trySaveMultipartFile(user.file)
        user.picture = savedFile

        return userRepository.save(user)
    }
}
