package ru.hlebozavod28.massak.DAO;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.Product;

@Repository
public interface ProductCrudRepository extends CrudRepository<Product, Long> {
}
