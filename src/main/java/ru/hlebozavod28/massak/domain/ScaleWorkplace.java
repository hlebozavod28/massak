package ru.hlebozavod28.massak.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "scaleworkplace", schema = "workplaces")
public class ScaleWorkplace {
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "scale", nullable = false)
    int scaleNumber;
    @Column(name = "workplace", nullable = false)
    int workPlace;

    public ScaleWorkplace(int scaleNumber, int workPlace) {
        this.scaleNumber = scaleNumber;
        this.workPlace = workPlace;
    }
}
