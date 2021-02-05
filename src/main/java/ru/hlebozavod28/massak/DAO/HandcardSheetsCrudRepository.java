package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.HandcardSheets;

@Repository
public interface HandcardSheetsCrudRepository extends CrudRepository<HandcardSheets, Long> {
    HandcardSheets getByHandcart(long handcart);
}
