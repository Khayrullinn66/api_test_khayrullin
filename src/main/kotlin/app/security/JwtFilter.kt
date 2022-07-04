package app.security

import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import app.exception.CustomException

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

class JwtFilter(private val jwtProvider: JwtProvider) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(httpServletRequest: HttpServletRequest,
                                  httpServletResponse: HttpServletResponse,
                                  filterChain: FilterChain) {
        var auth: Authentication? = null
        val token = jwtProvider.resolveToken(httpServletRequest)
        try {
            if (token != null && jwtProvider.validateToken(token)) {
                auth = jwtProvider.getAuthentication(token)
            }
        } catch (ex: CustomException) {
            log.info(ex.message)
            //this is very important, since it guarantees the user is not authenticated at all
/*            SecurityContextHolder.clearContext()
            httpServletResponse.status = ex.httpStatus.value()
            httpServletResponse.writer.write("{\"error\":\"${ex.message}\"}")
            return*/
        }
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(httpServletRequest, httpServletResponse)
    }

    companion object {
        private val log = LoggerFactory.getLogger(JwtFilter::class.java)
    }

}
