package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.ScaleWorkplace;

import java.util.List;

@Repository
public interface ScaleWorkplaceCrudRepository extends CrudRepository<ScaleWorkplace, Long> {
    List<ScaleWorkplace> findbyWorkplace(int wp);
}
