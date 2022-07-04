package app.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*
import javax.persistence.*
import javax.validation.constraints.Size


@Entity
class Animal  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Size(min = 4, max = 255, message = "Minimum nickname length: 4 characters")
    @Column(unique = true, nullable = false)
    var nickname: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    var race: Race? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var person: Person? = null

    @Column(unique = false, nullable = false)
    @Temporal(TemporalType.DATE)
    var birthDate: Date? = null

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    var gender: Gender? = null
}

enum class Gender {
    @JsonProperty("male")
    MALE,
    @JsonProperty("female")
    FEMALE;
}