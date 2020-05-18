package services

import models.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import repositories.UserRepository
import org.springframework.security.core.userdetails.User as SecurityUser
import org.springframework.security.core.userdetails.UserDetailsService as IUserDetailsService

@Service
class UserDetailsService(private val userRepository: UserRepository) : IUserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)

        println("loggin in... ----------------->")
        // println("size:: ${user.roles.size}") // <---- TODO() this cause infinite loop?
        val grantedAuthorities: MutableSet<SimpleGrantedAuthority> = mutableSetOf()
//        user.roles.forEach {role ->
//            grantedAuthorities.add(SimpleGrantedAuthority(role.name))
//        }

        return SecurityUser(user.username, user.password, grantedAuthorities)
    }
}