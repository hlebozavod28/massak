package ru.hlebozavod28.massak.domain

import javax.persistence.*

@Entity
@Table(name = "workplaces", schema = "workplaces")
class Workplace {
    @Id
    @GeneratedValue
    var id: Long = 0

    @Column(name = "workplace_name")
    var workPlaceName: String? = null

    @ManyToMany(mappedBy = "workplaces")
    lateinit var scales: MutableList<Scale>
}