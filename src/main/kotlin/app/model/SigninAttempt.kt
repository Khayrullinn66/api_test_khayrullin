package app.model


import java.util.*
import javax.persistence.*


@Entity
class SigninAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(unique = false, nullable = false)
    var ip: String? = null

    @Column(unique = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var creationDateTime: Date? = null
}