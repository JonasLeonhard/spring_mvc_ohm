package com.github.jonasleonhard.spring_mvc_ohm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.github.jonasleonhard.spring_mvc_ohm", "controllers", "models"])
class SpringMvcOhmApplication

fun main(args: Array<String>) {
    runApplication<SpringMvcOhmApplication>(*args)
}
