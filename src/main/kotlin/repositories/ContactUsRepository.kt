package repositories

import models.ContactUs
import org.springframework.data.jpa.repository.JpaRepository

interface ContactUsRepository : JpaRepository<ContactUs, Long> {
}