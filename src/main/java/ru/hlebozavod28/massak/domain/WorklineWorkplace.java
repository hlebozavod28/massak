package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "workline_workplaces", schema = "workplaces")
public class WorklineWorkplace {
    @Id
    @GeneratedValue
    long id;
    @Column(name = "workline_id", nullable = false)
    long worklineId;
    @Column(name = "workplace_id", nullable = false)
    long workplaceId;

    public WorklineWorkplace(long worklineId, long workplaceId) {
        this.worklineId = worklineId;
        this.workplaceId = workplaceId;
    }
}
