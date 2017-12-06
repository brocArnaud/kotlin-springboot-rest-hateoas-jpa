package org.demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.hateoas.config.EnableHypermediaSupport

@SpringBootApplication(scanBasePackages = arrayOf("org.demo"))
@EnableHypermediaSupport(type = arrayOf(EnableHypermediaSupport.HypermediaType.HAL))
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
