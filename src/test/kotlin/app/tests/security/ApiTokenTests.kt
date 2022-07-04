package app.tests.security

import io.restassured.RestAssured.given
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import app.security.JwtProvider
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_OK

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ApiTokenTests : app.tests.BaseTest() {

    @Test
    fun shouldAllowPublic() {
        val token = jwtProvider.createToken("testuser")
        given()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/test/public")
                .then()
                .statusCode(HTTP_OK)
                .body("result", `is`("ok"))
    }

    @Test
    fun shouldAllowProtected() {
        val token = jwtProvider.createToken("testuser")
        given()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/test/protected")
                .then()
                .statusCode(HTTP_OK)
                .body("result", `is`("ok"))
    }

    @Test
    fun shouldAllowPublicWrongSignedToken() {
        val token = JwtProvider(this.jwtProvider.validityInMilliseconds, "wrong key")
                .init()
                .createToken("testuser")

        given()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/test/public")
                .then()
                .statusCode(HTTP_OK)
                .body("result", `is`("ok"))
    }

    @Test
    fun shouldAllowPublicExpiredToken() {
        val token = JwtProvider(0, this.jwtProvider.secretKey)
                .init()
                .createToken("testuser")

        given()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/test/public")
                .then()
                .statusCode(HTTP_OK)
                .body("result", `is`("ok"))
    }

    @Test
    fun shouldRejectProtectedWrongSignedToken() {
        val token = JwtProvider(this.jwtProvider.validityInMilliseconds, "wrong key")
                .init()
                .createToken("testuser")

        given()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/test/protected")
                .then()
                .statusCode(HTTP_FORBIDDEN)
    }

    @Test
    fun shouldRejectProtectedExpiredToken() {
        val token = JwtProvider(0, this.jwtProvider.secretKey)
                .init()
                .createToken("testuser")

        given()
                .header("Authorization", "Bearer $token")
                .`when`()
                .get("/test/protected")
                .then()
                .statusCode(HTTP_FORBIDDEN)
    }
}

