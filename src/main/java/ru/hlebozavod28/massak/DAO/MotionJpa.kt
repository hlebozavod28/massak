package ru.hlebozavod28.massak.DAO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.hlebozavod28.massak.domain.Motion
import java.util.*

@Repository
interface MotionJpa : JpaRepository<Motion, Long?> {
    fun findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(workplaceId: Long, handcartId: Long): List<Motion>
    fun findByWorkplaceIdAndAmountNullAndDeletedFalse(workplaceId: Long): List<Motion>
    fun findFirstByWorkplaceIdAndHandcartIdAndDeletedFalseOrderByIdDesc(
        workplaceId: Long,
        handcartId: Long
    ): Motion?

    fun findFirstByWorkplaceIdAndDeletedFalseOrderByIdDesc(workplaceId: Long): Motion?
}

fun MotionJpa.findHandCart(workplaceId: Long, handcartId: Long): List<Motion>
    = findByWorkplaceIdAndHandcartIdAndAmountNullAndDeletedFalse(workplaceId, handcartId)