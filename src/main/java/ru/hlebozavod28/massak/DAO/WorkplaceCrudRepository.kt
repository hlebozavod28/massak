package ru.hlebozavod28.massak.DAO

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hlebozavod28.massak.domain.Workplace
import java.util.*

@Repository
interface WorkplaceCrudRepository : CrudRepository<Workplace, Long?> {
    fun findById(id: Long): Workplace?
}