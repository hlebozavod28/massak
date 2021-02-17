package ru.hlebozavod28.massak.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "weighting", schema = "workplaces")
class Weighting(
    @Column(name = "initial_weight", nullable = false)
    var initialWeight: Int,
    @Column(name = "scale_id", nullable = false)
    var scaleId: Long,
    @Column(name = "workplace_id", nullable = false)
    var workPlaceId: Long
) {
    @Id
    @GeneratedValue
    var id: Long = 0

    @Column(name = "final_weight")
    var finalWeight = 0

    @Column(name = "initial_timestamp", nullable = false)
    @CreationTimestamp
    var weightingTimestamp: Timestamp? = null

    @Column(name = "final_timestamp")
    @UpdateTimestamp
    var changeTimestamp: Timestamp? = null

    @Column(name = "completed")
    var completed = false

    @Column(name = "deleted")
    var deleted = false
}