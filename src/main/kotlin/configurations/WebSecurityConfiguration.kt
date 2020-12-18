package configurations

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Configuration
@EnableWebSecurity
class WebSecurityConfiguration(val userDetailsService: UserDetailsService) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers(
                        "/",
                        "/registration",
                        "/recipe/**",
                        "/js/**",
                        "/styles/**",
                        "/unsecured_files/**",
                        "/user/profile/**",
                        "/error/**",
                        "/login**",
                        "/logout",
                        "/about",
                        "/terms",
                        "/privacy",
                        "/contact",
                        "/resource/**",
                        "/search/**").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().loginPage("/login").and()
                .logout().invalidateHttpSession(true).clearAuthentication(true)
                .logoutUrl("/logout").logoutSuccessUrl("/login?logout=true").and()
                .httpBasic()
    }

    @Autowired
    @Throws(java.lang.Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder())
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun customAuthenticationManager(): AuthenticationManager? {
        return authenticationManager()
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }


}