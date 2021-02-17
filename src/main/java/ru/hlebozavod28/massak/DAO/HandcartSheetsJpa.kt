package ru.hlebozavod28.massak.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.HandcardSheets;

import java.util.Optional;

@Repository
public interface HandcardSheetsJpa extends JpaRepository<HandcardSheets, Long> {
    Optional<HandcardSheets> getByHandcart(long handcart);
}
