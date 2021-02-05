package ru.hlebozavod28.massak.DAO;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.WorklineWorkplace;

@Repository
public interface WorklineWorkplaceCrudRepository extends CrudRepository<WorklineWorkplace, Long> {
}
