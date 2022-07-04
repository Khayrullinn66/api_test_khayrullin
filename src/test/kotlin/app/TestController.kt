package app

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/test")
class TestController {

    @RequestMapping("/public")
    fun public() = object {val result = "ok"}

    @RequestMapping("/protected")
    fun protected() = object {val result = "ok"}
}