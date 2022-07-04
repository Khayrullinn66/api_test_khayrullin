package app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import app.security.JwtProvider
import app.security.WebSecurityConfig


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("test")
class WebSecurityConfigTest(@Autowired  private val jwtProvider: JwtProvider)
    : WebSecurityConfig(jwtProvider) {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/test/public").permitAll()
                .antMatchers("/test/protected").authenticated()
        super.configure(http)
    }
}
