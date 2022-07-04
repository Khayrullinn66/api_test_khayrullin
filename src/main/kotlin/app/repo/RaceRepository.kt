package app.repo

import org.springframework.data.repository.CrudRepository
import app.model.Race


interface RaceRepository : CrudRepository<Race, Int> {

}