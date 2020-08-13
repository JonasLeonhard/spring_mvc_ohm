package repositories

import models.UserRecipeBuyList
import org.springframework.data.jpa.repository.JpaRepository

interface UserRecipeBuyListRepository : JpaRepository<UserRecipeBuyList, Long>