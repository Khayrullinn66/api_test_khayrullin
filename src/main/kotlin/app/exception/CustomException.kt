package app.exception

import app.exception.Type.UNKNOWN_ERROR
import java.net.HttpURLConnection.*

class CustomException(val error: Type = UNKNOWN_ERROR) : RuntimeException(error.desc) {

    companion object {
        private const val serialVersionUID = 1L
    }

    @Synchronized
    override fun fillInStackTrace(): Throwable {
        return this
    }
}

enum class Type(val errorCode: Int, val desc: String, val httpCode: Int) {
    UNKNOWN_ERROR(1, "Something went wrong", HTTP_INTERNAL_ERROR),
    INVALID_USERNAME_PASSWORD(2, "Invalid username/password supplied", HTTP_UNAUTHORIZED),
    USERNAME_ALREADY_IN_USE(3, "Username is already in use", HTTP_CONFLICT),
    TOO_MANY_SIGNIN_ATTEMPTS(4, "Too many signin attempts", HTTP_FORBIDDEN),
    INVALID_JWT_TOKEN(5, "Expired or invalid JWT token", HTTP_UNAUTHORIZED),
    ANIMAL_NOT_OWNED(6, "Animal belongs to another user", HTTP_UNAUTHORIZED),
    NOT_FOUND(7, "Not found", HTTP_NOT_FOUND),
    NICKNAME_ALREADY_IN_USE(8, "Animal`s nickname is already in use", HTTP_CONFLICT);
}