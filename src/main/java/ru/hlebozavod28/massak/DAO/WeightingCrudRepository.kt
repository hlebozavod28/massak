package ru.hlebozavod28.massak.DAO

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hlebozavod28.massak.domain.Weighting

@Repository
interface WeightingCrudRepository : CrudRepository<Weighting, Long?> {
    fun findByScaleIdAndWorkPlaceIdAndCompletedAndDeleted(
        scale_id: Long,
        workplace_id: Long,
        completed: Boolean,
        deleted: Boolean
    ): List<Weighting>
}

fun WeightingCrudRepository.findWeightings(
    scale_id: Long,
    workplace_id: Long,
    completed: Boolean = false,
    deleted: Boolean = false
) = findByScaleIdAndWorkPlaceIdAndCompletedAndDeleted(scale_id, workplace_id, completed, deleted)