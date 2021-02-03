package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "scales", schema = "workplaces")
public class Scale {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "ipaddr", nullable = false, unique = true)
    private String ipaddr;
    @ManyToMany(targetEntity = Workplace.class)
    @JoinTable(name = "scale_workplace", schema = "workplaces",
            joinColumns = @JoinColumn(name = "scale_id"), inverseJoinColumns = @JoinColumn(name = "workplace_id"))
    private List<Workplace> workplaces;
}
