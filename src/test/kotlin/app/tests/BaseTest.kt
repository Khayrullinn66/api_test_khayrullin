package app.tests

import io.restassured.RestAssured
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import app.model.Animal
import app.model.Gender
import app.model.Race
import app.model.Person
import app.repo.AnimalRepository
import app.repo.RaceRepository
import app.repo.UserRepository
import app.security.JwtProvider
import java.util.*

abstract class BaseTest {
    @Autowired
    lateinit var animalRepository: AnimalRepository
    @Autowired
    lateinit var raceRepository: RaceRepository
    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var jwtProvider: JwtProvider

    @Before
    fun configureRestAssured() {
        RestAssured.reset()
        RestAssured.baseURI = "http://localhost:8080"
        RestAssured.filters(
                RequestLoggingFilter(),
                ResponseLoggingFilter())
    }

    @Before
    fun resetDb() {
        animalRepository.deleteAll()
        raceRepository.deleteAll()
        userRepository.deleteAll()
    }

    protected fun createDbUser(): Person {
        return userRepository
                .save(Person()
                        .apply {
                            username = "tester_" + Random().nextInt(Int.MAX_VALUE)
                            email = "test_" + Random().nextInt(Int.MAX_VALUE) + "@test.ru"
                            password = Random().nextInt(Int.MAX_VALUE).toString()
                        }
                )
    }

    protected fun createDbRace(): Race {
        return raceRepository.save(Race().apply { name = "race_" + Random().nextInt(Int.MAX_VALUE) })
    }

    protected fun createDbAnimal(person: Person, race: Race): Animal {
        return animalRepository.save(Animal().apply {
            birthDate = Date()
            nickname = "nick_" + Random().nextInt(Int.MAX_VALUE)
            gender = Gender.MALE
            this.race = race
            this.person = person
        })
    }
}