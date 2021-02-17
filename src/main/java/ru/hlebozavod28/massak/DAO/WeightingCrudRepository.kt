package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.Weighting;

import java.util.List;

@Repository
public interface WeightingCrudRepository extends CrudRepository<Weighting, Long> {
    List<Weighting> findByScaleIdAndWorkPlaceIdAndCompletedAndDeleted(long scale_id, long workplace_id, boolean completed, boolean deleted);
}
