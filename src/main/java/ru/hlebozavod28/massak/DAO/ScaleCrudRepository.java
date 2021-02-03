package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.Scale;

import java.util.Optional;

@Repository
public interface ScaleCrudRepository extends CrudRepository<Scale, Long> {
    Optional<Scale> getById(long id);
}
