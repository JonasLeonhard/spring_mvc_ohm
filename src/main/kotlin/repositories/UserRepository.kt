package repositories

import models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun findAllByUsername(username: String): Set<User>

    @Query("SELECT user from User user WHERE lower(user.username) LIKE lower(concat('%', ?1, '%'))")
    fun searchAllByUsername(username: String): Set<User>
}