package app.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import app.repo.UserRepository

@Service
class MyUserDetails(@Autowired private val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository
                .findByUsername(username)
                ?: throw UsernameNotFoundException("User '$username' not found")

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.password)
                .authorities(user.roles)//
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build()
    }

}
