package ru.hlebozavod28.massak.DAO

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.hlebozavod28.massak.domain.Product

@Repository
interface ProductCrudRepository : CrudRepository<Product, Long?>