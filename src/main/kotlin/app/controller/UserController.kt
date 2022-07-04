package app.controller

import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Autowired

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import app.model.ApiToken
import app.model.Person
import app.service.UserService

@RestController
@RequestMapping("/users")
@Api(tags = ["users"])
class UserController(@Autowired private val userService: UserService) {

    @PostMapping("/signin")
    @ApiOperation(value = "\${UserController.signin}", response = ApiToken::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "signed in"),
        ApiResponse(code = 400, message = "Something went wrong"),
        ApiResponse(code = 422, message = "Invalid username/password supplied")
    ])

    fun signin(@RequestBody person: Person, req: HttpServletRequest): ApiToken {
        return userService.signin(person, req)
    }

    @PostMapping("/signup")
    @ApiOperation(value = "\${UserController.signup}", response = ApiToken::class)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = [
        ApiResponse(code = 201, message = "user created"),
        ApiResponse(code = 500, message = "Something went wrong")
    ])
    fun signup(@ApiParam("Signup User") @RequestBody person: Person): ApiToken {
        return userService.signup(person)
    }

    @GetMapping(value = ["/me"])
    @ApiOperation(value = "\${UserController.me}", response = Person::class)
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "ok"),
        ApiResponse(code = 404, message = "Not found"),
        ApiResponse(code = 500, message = "Something went wrong")
    ])

    fun whoami(req: HttpServletRequest): Person {
        return userService.whoami(req)
    }

    @GetMapping("/refresh")
    fun refresh(req: HttpServletRequest): ApiToken {
        return userService.refresh(req)
    }

}
