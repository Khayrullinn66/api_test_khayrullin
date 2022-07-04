package app.model


import javax.persistence.*


@Entity
class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(unique = true, nullable = false)
    var name: String? = null
}