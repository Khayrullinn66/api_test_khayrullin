package app

import app.model.Race
import app.repo.RaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class Application : CommandLineRunner {
    @Autowired
    lateinit var raceRepository: RaceRepository

    override fun run(vararg args: String?) {
        raceRepository.save(Race().apply { name = "dogs" })
        raceRepository.save(Race().apply { name = "cats" })
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java)
        }
    }
}

