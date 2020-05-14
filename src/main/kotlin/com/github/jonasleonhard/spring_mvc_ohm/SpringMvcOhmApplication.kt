package com.github.jonasleonhard.spring_mvc_ohm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.github.jonasleonhard.spring_mvc_ohm", "controllers", "models", "repositories", "daos"])
@EntityScan("models")
@EnableJpaRepositories(basePackages = ["repositories"])
class SpringMvcOhmApplication

fun main(args: Array<String>) {
    runApplication<SpringMvcOhmApplication>(*args)
}
