package ru.hlebozavod28.massak.CMLRunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MassaKTestRead implements CommandLineRunner{
    @Override
    public void run(String... args){
        log.info(System.getProperty("java.library.path"));
    }
}
