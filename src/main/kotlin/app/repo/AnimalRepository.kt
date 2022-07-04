package app.repo

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import app.model.Animal
import javax.transaction.Transactional


interface AnimalRepository : CrudRepository<Animal, Int> {
    @Query("SELECT a FROM Animal a LEFT JOIN FETCH a.race LEFT JOIN FETCH a.person WHERE a.person.id = :userId")
    fun findByUserId(@Param("userId") userId: Int?): List<Animal>?

    @Query("SELECT a FROM Animal a LEFT JOIN FETCH a.race LEFT JOIN FETCH a.person WHERE a.id = :animalId")
    fun findByAnimalId(@Param("animalId") animalId: Int?): Animal?

    @Transactional
    @Modifying
    @Query("DELETE FROM Animal a WHERE a.person.id = :userId AND a.id = :animalId")
    fun deleteByUserIdAndAnimalId(@Param("userId") userId: Int?, @Param("animalId") animalId: Int?)

    fun existsByNickname(nickname: String?): Boolean
}