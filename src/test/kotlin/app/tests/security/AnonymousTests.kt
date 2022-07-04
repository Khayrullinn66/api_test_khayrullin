package app.tests.security

import io.restassured.RestAssured.given
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_OK

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AnonymousTests : app.tests.BaseTest() {

    @Test
    fun shouldAllowPublic() {
        given()
                .`when`()
                .get("/test/public")
                .then()
                .statusCode(HTTP_OK)
                .body("result", `is`("ok"))
    }

    @Test
    fun testRejectProtected() {
        given()
                .`when`()
                .get("/test/protected")
                .then()
                .statusCode(HTTP_FORBIDDEN)
    }
}

