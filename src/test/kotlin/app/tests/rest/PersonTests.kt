package app.tests.rest

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import app.exception.Type
import app.model.SigninAttempt
import app.repo.SigninAttemptRepository
import app.service.UserService
import java.net.HttpURLConnection.*
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PersonTests : app.tests.BaseTest() {

    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var signinAttemptRepository: SigninAttemptRepository

    @Test
    fun shouldSignupAndSigninAndEnterProtected() {
        val username = "tester" + Random().nextInt(Int.MAX_VALUE)
        val email = "test" + Random().nextInt(Int.MAX_VALUE) + "@test.ru"
        val password = Random().nextInt(Int.MAX_VALUE)

        var token = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"$username\", \"email\":\"$email\", \"password\":\"$password\"}")
                .`when`()
                .post("/users/signup")
                .then()
                .statusCode(HTTP_CREATED)
                .body("token", notNullValue())
                .body("expiresIn", greaterThan(1))
                .body("tokenType", `is`("Bearer"))
                .extract()
                .body()
                .jsonPath()
                .getString("token")

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/users/me")
                .then()
                .statusCode(HTTP_OK)
                .body("id", greaterThan(0))
                .body("username", `is`(username))
                .body("email", `is`(email))

        token = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"$username\", \"password\":\"$password\"}")
                .`when`()
                .post("/users/signin")
                .then()
                .statusCode(HTTP_OK)
                .body("token", notNullValue())
                .body("expiresIn", greaterThan(1))
                .body("tokenType", `is`("Bearer"))
                .extract()
                .body()
                .jsonPath()
                .getString("token")

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/users/me")
                .then()
                .statusCode(HTTP_OK)
                .body("id", greaterThan(0))
                .body("username", `is`(username))
                .body("email", `is`(email))

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/users/refresh")
                .then()
                .statusCode(HTTP_OK)
                .body("token", notNullValue())
                .body("expiresIn", greaterThan(1))
                .body("tokenType", `is`("Bearer"))
    }

    @Test
    fun shouldRejectWhenBannedAndSigninWhenBanExpires() {
        for (i in 1..userService.maxTries) {
            val attempt = SigninAttempt().apply { creationDateTime = Date(); ip = "another ip" }
            signinAttemptRepository.save(attempt)
        }

        val username = "tester" + Random().nextInt(Int.MAX_VALUE)
        val email = "test" + Random().nextInt(Int.MAX_VALUE) + "@test.ru"
        val password = Random().nextInt(Int.MAX_VALUE)

        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"$username\", \"email\":\"$email\", \"password\":\"$password\"}")
                .`when`()
                .post("/users/signup")
                .then()
                .statusCode(HTTP_CREATED)

        for (i in 1..userService.maxTries) {
            given()
                    .contentType(ContentType.JSON)
                    .body("{\"username\":\"$username\", \"password\":\"wrong\"}")
                    .`when`()
                    .post("/users/signin")
                    .then()
                    .statusCode(HTTP_UNAUTHORIZED)
                    .body("errorCode", `is`(Type.INVALID_USERNAME_PASSWORD.errorCode))
        }
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"$username\", \"password\":\"wrong\"}")
                .`when`()
                .post("/users/signin")
                .then()
                .statusCode(HTTP_FORBIDDEN)
                .body("errorCode", `is`(Type.TOO_MANY_SIGNIN_ATTEMPTS.errorCode))

        Thread.sleep(userService.expireTime.toLong())

        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"$username\", \"password\":\"$password\"}")
                .`when`()
                .post("/users/signin")
                .then()
                .statusCode(HTTP_OK)
    }
}