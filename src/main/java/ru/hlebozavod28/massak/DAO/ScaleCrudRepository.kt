package ru.hlebozavod28.massak.DAO

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hlebozavod28.massak.domain.Scale
import java.util.*

@Repository
interface ScaleCrudRepository : CrudRepository<Scale, Long?> {
    fun getById(id: Long): Optional<Scale>
}