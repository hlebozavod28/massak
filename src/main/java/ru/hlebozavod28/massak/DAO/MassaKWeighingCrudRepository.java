package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.MassaKWeighing;

import java.util.Optional;

@Repository
public interface MassaKWeighingCrudRepository extends CrudRepository<MassaKWeighing, Long> {
    Optional<MassaKWeighing> getByWeighingworkplaceAndSecondweight(int wp, int secondwaight);
}
