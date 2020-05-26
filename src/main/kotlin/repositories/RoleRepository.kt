package repositories

import models.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    @Query("SELECT role FROM Role role where role.name = :roleName")
    fun findByName(@Param("roleName") roleName: String?): Role?
}