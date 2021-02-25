package ru.hlebozavod28.massak.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.ProdExec;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface ProdExecJpa extends JpaRepository<ProdExec, Long> {
    Optional<ProdExec> findFirstByProd_dateProd_smenaProd_id(Date prodDate, int prodSmena, int prodId);

}
