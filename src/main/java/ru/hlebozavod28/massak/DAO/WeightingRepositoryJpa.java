package ru.hlebozavod28.massak.DAO;

import ru.hlebozavod28.massak.domain.Weighting;

public interface WeightingRepositoryJpa {
    public Weighting getByScaleWorkplaceFinalNul(long scale, long workplace);
}
