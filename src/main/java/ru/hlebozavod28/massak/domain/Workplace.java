package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "workplaces", schema = "workplaces")
public class Workplace {
    @Id
    @GeneratedValue
    long id;
    @Column(name = "workplace_name")
    String WorkPlaceName;
    @ManyToMany(mappedBy = "workplaces")
    List<Scale> scales;
}
