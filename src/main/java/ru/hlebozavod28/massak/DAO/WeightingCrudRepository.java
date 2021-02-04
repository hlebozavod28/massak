package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import ru.hlebozavod28.massak.domain.Weighting;

import java.util.List;

public interface WeightingCrudRepository extends CrudRepository<Weighting, Long> {
    List<Weighting> findByScaleIdAndWorkPlaceIdAndCompleted(long scale_id, long workplace_id, boolean completed);
}
