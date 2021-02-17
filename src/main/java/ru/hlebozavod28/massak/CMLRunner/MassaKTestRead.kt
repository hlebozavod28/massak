package ru.hlebozavod28.massak.CMLRunner

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class MassaKTestRead : CommandLineRunner {
    private val log = Logger.getLogger(MassaKTestRead::class.java.name)
    override fun run(vararg args: String) {
//        log.info(System.getProperty("java.library.path"));
    }
}