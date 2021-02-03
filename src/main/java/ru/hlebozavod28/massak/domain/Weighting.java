package ru.hlebozavod28.massak.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "weighting", schema = "workplaces")
public class Weighting {
    @Id
    @GeneratedValue
    long id;
    @Column(name = "initial_weight", nullable = false)
    int initialWeight;
    @Column(name = "final_weight")
    int finalWeight;
    @Column(name = "weighting_timestamp", nullable = false)
    @CreationTimestamp
    Timestamp weightingTimestamp;
    @Column(name = "scale_id", nullable = false)
    long scaleId;
    @Column(name = "workplace_id", nullable = false)
    long workPlaceId;

    public Weighting(int initialWeight, long scaleId, long workPlaceId) {
        this.initialWeight = initialWeight;
        this.scaleId = scaleId;
        this.workPlaceId = workPlaceId;
    }
}
