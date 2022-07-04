package app.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import app.exception.CustomException
import app.exception.Type
import java.util.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Component
class JwtProvider(@Value("\${security.jwt.token.expire-length}") val validityInMilliseconds: Long,
                  @Value("\${security.jwt.token.secret-key}") var secretKey: String) {

    @PostConstruct
    fun init(): JwtProvider {
        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
        return this
    }

    fun createToken(username: String): String {
        val claims = Jwts.claims().setSubject(username)
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val username = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
        return UsernamePasswordAuthenticationToken(username, null, emptyList())
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            return true
        } catch (e: JwtException) {
            throw CustomException(Type.INVALID_JWT_TOKEN)
        } catch (e: IllegalArgumentException) {
            throw CustomException(Type.INVALID_JWT_TOKEN)
        }

    }

}

