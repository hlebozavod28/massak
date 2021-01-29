package ru.hlebozavod28.massak.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "scales", schema = "workplaces")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MassaKScale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "ipaddr", nullable = false, unique = true)
    private String ipaddr;

    public MassaKScale(String ipaddr) {
        this.ipaddr = ipaddr;
    }
}
