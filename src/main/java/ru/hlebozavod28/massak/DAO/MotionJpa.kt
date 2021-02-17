package ru.hlebozavod28.massak.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.Motion;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotionJpa extends JpaRepository<Motion, Long> {
    List<Motion> findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(long workplaceId, long handcartId);
    List<Motion> findByWorkplaceIdAndAmountNullAndDeletedFalse(long workplaceId);
    Optional<Motion> findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(long workplaceId, long handcartId);
    Optional<Motion> findFirstByWorkplaceIdAndDeletedFalseOrderByIdDesc(long workplaceId);
}
