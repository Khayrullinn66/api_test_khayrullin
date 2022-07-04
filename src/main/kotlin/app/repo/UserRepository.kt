package app.repo

import org.springframework.data.jpa.repository.JpaRepository
import app.model.Person

interface UserRepository : JpaRepository<Person, Int> {

    fun existsByUsername(username: String?): Boolean

    fun findByUsername(username: String?): Person?
}