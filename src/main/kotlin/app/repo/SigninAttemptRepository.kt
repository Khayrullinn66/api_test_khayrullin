package app.repo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import app.model.SigninAttempt
import java.util.*
import javax.transaction.Transactional


interface SigninAttemptRepository : CrudRepository<SigninAttempt, Int> {

    @Query("SELECT COUNT(sa) FROM SigninAttempt sa WHERE sa.creationDateTime > :creationDateTime AND sa.ip = :ip")
    fun countAfter(@Param("creationDateTime") creationDateTime: Date, @Param("ip") ip: String): Int

    @Transactional
    fun deleteByIp(ip: String)
}