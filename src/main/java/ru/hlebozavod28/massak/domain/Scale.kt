package ru.hlebozavod28.massak.domain

import javax.persistence.*

@Entity
@Table(name = "scales", schema = "workplaces")
class Scale (
    @Id
    @GeneratedValue
    var id: Long
    )
{
    @Column(name = "ipaddr", nullable = false, unique = true)
    var ipaddr: String? = null

    @ManyToMany(targetEntity = Workplace::class)
    @JoinTable(
        name = "scale_workplace",
        schema = "workplaces",
        joinColumns = [JoinColumn(name = "scale_id")],
        inverseJoinColumns = [JoinColumn(name = "workplace_id")]
    )
    private val workplaces: MutableList<Workplace>? = null
}