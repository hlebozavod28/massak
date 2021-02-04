package ru.hlebozavod28.massak.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
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
    @Column(name = "change_timestamp", updatable = false)
    @UpdateTimestamp
    Timestamp changeTimestamp;
    @Column(name = "scale_id", nullable = false)
    long scaleId;
    @Column(name = "workplace_id", nullable = false)
    long workPlaceId;
    @Column(name = "completed")
    boolean completed;

    public Weighting(int initialWeight, long scaleId, long workPlaceId) {
        this.initialWeight = initialWeight;
        this.scaleId = scaleId;
        this.workPlaceId = workPlaceId;
    }
}
