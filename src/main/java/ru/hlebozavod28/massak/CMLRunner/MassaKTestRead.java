package ru.hlebozavod28.massak.CMLRunner;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class MassaKTestRead implements CommandLineRunner{
    @Override
    public void run(String... args){
        log.info(System.getProperty("java.library.path"));
    }
}
