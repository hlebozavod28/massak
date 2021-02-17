package ru.hlebozavod28.massak.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "handcart_motion", schema = "workplaces")
class Motion(
    @Column(name = "workplace_id", nullable = false)
    var workplaceId: Long ,
    @Column(name = "handcart_id", nullable = false)
    var handcartId: Long ,
    @Column(name = "product_code")
    var productCode: Int ,
    @Column(name = "sheets")
    var sheets: Int
) {
    @Id
    @GeneratedValue
    var id: Long = 0

    @Column(name = "sub_workplace")
    var subWorkplace = 0

    @CreationTimestamp
    @Column(name = "in_timestamp", nullable = false, updatable = false)
    var inTs: Timestamp? = null

    @UpdateTimestamp
    @Column(name = "out_timestamp")
    var outTs: Timestamp? = null

    @Column(name = "amount")
    var amount: BigDecimal? = null

    @Column(name = "defect_count")
    var defectCount: BigDecimal? = null

    @Column(name = "deleted")
    var deleted = false
}