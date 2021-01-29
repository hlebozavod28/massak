package ru.hlebozavod28.massak.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "scaleweighing", schema = "workplaces")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MassaKWeighing {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "startweight")
    int startweight;

    @Column(name = "secondweight")
    int secondweight;

    @Column(name = "weighingdatetime", updatable = false, nullable = false)
    @CreationTimestamp
    Timestamp weighingdatetime;

    @Column(name = "updatedatetime")
    @UpdateTimestamp
    Timestamp updatedatetime;

    @Column(name = "weighingworkplace")
    int weighingworkplace;

    public MassaKWeighing(int startweight, int secondweight, int weighingworkplace) {
        this.startweight = startweight;
        this.secondweight = secondweight;
        this.weighingworkplace = weighingworkplace;
    }
}
