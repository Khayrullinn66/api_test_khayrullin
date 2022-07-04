package app.model


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.core.GrantedAuthority
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Size(min = 4, max = 255, message = "Minimum username length: 4 characters")
    @Column(unique = true, nullable = false)
    var username: String? = null

    @Column(unique = true, nullable = false)
    var email: String? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, message = "Minimum password length: 8 characters")
    var password: String? = null

    @JsonIgnore
    @Transient
    val roles = emptyList<GrantedAuthority>()
}