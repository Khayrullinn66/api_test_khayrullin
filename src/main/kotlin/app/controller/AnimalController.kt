package app.controller


import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import app.model.Animal
import app.service.AnimalService
import app.service.UserService
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/animals")
@Api(tags = ["animals"])
class AnimalController(@Autowired private val userService: UserService,
                       @Autowired private val animalService: AnimalService) {

    @PostMapping
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "Animal is created"),
        ApiResponse(code = 401, message = "Animal belongs to another user"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ApiOperation(value = "\${AnimalController.create}", response = Animal::class)
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody animal: Animal, req: HttpServletRequest): Animal {
        animal.apply { person = userService.whoami(req) }
        return animalService.create(animal)
    }

    @GetMapping
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Animals are listed"),
        ApiResponse(code = 401, message = "Animal belongs to another user"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ApiOperation(value = "\${AnimalController.list}", response = Animal::class, responseContainer = "List")
    fun list(req: HttpServletRequest): List<Animal>? {
        val user = userService.whoami(req)
        return animalService.findAll(user)
    }

    @GetMapping(value = ["/{id}"])
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Animal is returned"),
        ApiResponse(code = 401, message = "Animal belongs to another user"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ApiOperation(value = "\${AnimalController.get}", response = Animal::class)
    fun get(@PathVariable("id") id: Int, req: HttpServletRequest): Animal {
        val user = userService.whoami(req)
        return animalService.findAndCheck(id, user)
    }

    @DeleteMapping(value = ["/{id}"])
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Animal is successfully deleted"),
        ApiResponse(code = 401, message = "Animal belongs to another user"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ApiOperation(value = "\${AnimalController.delete}", response = Animal::class)
    fun delete(@PathVariable("id") id: Int, req: HttpServletRequest) {
        val user = userService.whoami(req)
        val animalToDelete = animalService.findAndCheck(id, user)
        animalService.delete(animalToDelete)
    }

    @PutMapping(value = ["/{id}"])
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "Animal successfully updated"),
        ApiResponse(code = 401, message = "Animal belongs to another user"),
        ApiResponse(code = 404, message = "Not found")
    ])
    @ApiOperation(value = "\${AnimalController.update}", response = Animal::class)
    fun update(@PathVariable("id") id: Int, @RequestBody animal: Animal, req: HttpServletRequest) {
        val user = userService.whoami(req)
        animalService.findAndCheck(id, user)
        animalService.update(animal.apply { this.id = id })

    }
}