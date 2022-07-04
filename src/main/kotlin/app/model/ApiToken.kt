package app.model

data class ApiToken(
        val token: String,
        val expiresIn: Long,
        val tokenType: String = "Bearer"
)