package app.tests.rest

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import app.exception.Type
import app.model.*
import app.tests.BaseTest
import java.net.HttpURLConnection.*
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AnimalTests : BaseTest() {

    @Test
    fun shouldCreateAnimal() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val token = jwtProvider.createToken(me.username!!)
        val nick = "nick_" + Random().nextInt(Int.MAX_VALUE)

        // when
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .body("{\"nickname\": \"$nick\",\"race\": {\"id\": ${race.id}}," +
                        "\"birthDate\": \"2015-02-13\",\"gender\": \"female\"}")
                .`when`()
                .post("/animals")
                .then()
                .statusCode(HTTP_CREATED)

        // then
        val animals = animalRepository.findAll().toList()
        assert(animals.size == 1)
        assert(animals.first().nickname == nick)
        assert(animals.first().gender == Gender.FEMALE)
        assert(animals.first().race!!.name == race.name)
        assert(animals.first().birthDate.toString().equals("2015-02-13"))
    }

    @Test
    fun shouldGetAnimal() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val myAnimal = createDbAnimal(me, race)
        val token = jwtProvider.createToken(me.username!!)

        // then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/animals/${myAnimal.id}")
                .then()
                .statusCode(HTTP_OK)
                .body("nickname", `is`(myAnimal.nickname))
                .body("race.name", `is`(myAnimal.race!!.name))
                .body("gender", `is`("male"))
    }

    @Test
    fun shouldListAnimals() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        for (i in 0..1) {
            createDbAnimal(me, race)
        }

        val otherUser = createDbUser()
        createDbAnimal(otherUser, race)

        val token = jwtProvider.createToken(me.username!!)

        // then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/animals")
                .then()
                .statusCode(HTTP_OK)
                .body("size", `is`(2))
    }


    @Test
    fun shouldDeleteAnimal() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val myAnimal = createDbAnimal(me, race)
        val token = jwtProvider.createToken(me.username!!)

        // then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .delete("/animals/${myAnimal.id}")
                .then()
                .statusCode(HTTP_OK)

        assert(animalRepository.findAll().toList().isEmpty())
    }

    @Test
    fun shouldUpdateAnimal() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val raceNew = createDbRace()
        val nickNew = "nick_" + Random().nextInt(Int.MAX_VALUE)
        val myAnimal = createDbAnimal(me, race)
        val token = jwtProvider.createToken(me.username!!)

        // when
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .body("{\"nickname\": \"$nickNew\",\"race\": {\"id\": ${raceNew.id}}," +
                        "\"birthDate\": \"2010-01-01\",\"gender\": \"female\"}")
                .`when`()
                .put("/animals/${myAnimal.id}")
                .then()
                .statusCode(HTTP_OK)

        // then
        val animals = animalRepository.findAll().toList()
        assert(animals.size == 1)
        assert(animals.first().nickname == nickNew)
        assert(animals.first().gender == Gender.FEMALE)
        assert(animals.first().race!!.name == raceNew.name)
        assert(animals.first().birthDate.toString().equals("2010-01-01"))
    }

    @Test
    fun shouldFailUpdateNotOwnedAnimal() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val raceNew = createDbRace()
        val nickNew = "nick_" + Random().nextInt(Int.MAX_VALUE)
        val notOwnedAnimal = createDbAnimal(createDbUser(), race)
        val token = jwtProvider.createToken(me.username!!)

        // when
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .body("{\"nickname\": \"$nickNew\",\"race\": {\"id\": ${raceNew.id}}," +
                        "\"birthDate\": \"2010-01-01\",\"gender\": \"female\"}")
                .`when`()
                .put("/animals/${notOwnedAnimal.id}")
                .then()
                .statusCode(HTTP_UNAUTHORIZED)

        // then
        val animals = animalRepository.findAll().toList()
        assert(animals.size == 1)
        assert(animals.first().nickname == notOwnedAnimal.nickname)
        assert(animals.first().gender == Gender.MALE)
        assert(animals.first().race!!.name == notOwnedAnimal.race!!.name)
    }

    @Test
    fun shouldFailGetNotOwnedAnimal() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val notOwnedAnimal = createDbAnimal(createDbUser(), race)
        val token = jwtProvider.createToken(me.username!!)

        // then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/animals/${notOwnedAnimal.id}")
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("errorCode", `is`(Type.ANIMAL_NOT_OWNED.errorCode))
    }

    @Test
    fun shouldFailDeleteNotOwnedAnimal() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val notOwnedAnimal = createDbAnimal(createDbUser(), race)
        val token = jwtProvider.createToken(me.username!!)

        // then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .delete("/animals/${notOwnedAnimal.id}")
                .then()
                .statusCode(HTTP_UNAUTHORIZED)
                .body("errorCode", `is`(Type.ANIMAL_NOT_OWNED.errorCode))

        assert(animalRepository.findByAnimalId(notOwnedAnimal.id) != null)
    }


    @Test
    fun shouldFailGetNotExistingAnimal() {
        // given
        val me = createDbUser()
        val token = jwtProvider.createToken(me.username!!)

        // then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/animals/999")
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("errorCode", `is`(Type.NOT_FOUND.errorCode))
    }

    @Test
    fun shouldFailCreateAnimalWithConflictName() {
        // given
        val me = createDbUser()
        val race = createDbRace()
        val notOwnedAnimal = createDbAnimal(createDbUser(), race)


        val token = jwtProvider.createToken(me.username!!)

        // then
        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .body("{\"nickname\": \"${notOwnedAnimal.nickname}\",\"race\": {\"id\": ${race.id}}," +
                        "\"birthDate\": \"2015-02-13\",\"gender\": \"female\"}")
                .`when`()
                .post("/animals")
                .then()
                .statusCode(HTTP_CONFLICT)
    }
}