package ru.hlebozavod28.massak.DAO;

import org.springframework.stereotype.Repository;
import ru.hlebozavod28.massak.domain.Weighting;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class WeightingRepositoryJpaImpl implements WeightingRepositoryJpa{
    @PersistenceContext
    private EntityManager em;

    @Override
    public Weighting getByScaleWorkplaceFinalNul(long scale, long workplace) {
        return null;
    }
}
