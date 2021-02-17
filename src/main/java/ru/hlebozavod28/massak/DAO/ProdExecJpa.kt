package ru.hlebozavod28.massak.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.ProdExec;

@Repository
public interface ProdExecJpa extends JpaRepository<ProdExec, Long> {
}
