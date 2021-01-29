package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.MassaKScale;
import java.util.Optional;

@Repository
public interface MassaKScaleCrudRepository extends CrudRepository<MassaKScale, Long> {
    Optional<MassaKScale> getById(long id);
}
