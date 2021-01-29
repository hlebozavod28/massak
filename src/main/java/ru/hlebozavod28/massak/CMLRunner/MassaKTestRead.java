package ru.hlebozavod28.massak.CMLRunner;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.hlebozavod28.massak.DAO.MassaKScaleCrudRepository;
import ru.hlebozavod28.massak.domain.MassaKScale;

import java.util.Optional;

@Log4j2
@Component
public class MassaKTestRead implements CommandLineRunner{
    @Autowired
    private MassaKScaleCrudRepository massaKScaleCrudRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info(System.getProperty("java.library.path"));
        //MassaKScale massaKScale1 = new MassaKScale(1, 1, "10.1.230.16");
        //massaKScaleCrudRepository.save(massaKScale1);
        //MassaKScale massaKScale = massaKScaleCrudRepository.getByWorkplace(1).get();
        //log.info(massaKScale.getIpaddr());
    }
}
