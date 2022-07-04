package app.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class HealthController {
    @RequestMapping("/health")
    fun health() = object {val result = "ok"}
}