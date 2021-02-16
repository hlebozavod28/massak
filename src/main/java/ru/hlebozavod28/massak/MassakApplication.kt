package ru.hlebozavod28.massak

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication

@SpringBootApplication
class MassakApplication

fun main(args: Array<String>) {
    SpringApplication.run(MassakApplication::class.java, *args)
}