package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.Motion;

import java.util.List;

@Repository
public interface MotionCrudRepository extends CrudRepository<Motion, Long> {
    List<Motion> findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(long workplaceId, long handcartId);

}
