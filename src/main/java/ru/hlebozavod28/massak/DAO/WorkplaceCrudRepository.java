package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.Workplace;

import java.util.Optional;

@Repository
public interface WorkplaceCrudRepository extends CrudRepository<Workplace, Long> {
    Optional<Workplace> findById(long id);

}
