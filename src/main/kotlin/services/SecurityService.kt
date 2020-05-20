package services

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class SecurityService(val authenticationManager: AuthenticationManager, val userDetailsService: UserDetailsService) {

    fun findLoggedInUsername(): String? {
        val userDetails: Any? = SecurityContextHolder.getContext().authentication.details
        return if (userDetails is UserDetails) userDetails.username else null
    }

    fun autoLogin(username: String, password: String) {
        println("TODO() - utologin called!")
        val userDetails = userDetailsService.loadUserByUsername(username)
        val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(userDetails, password)

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated) {
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken;
            println("Auto login of $username successfully!")
        }
    }
}

