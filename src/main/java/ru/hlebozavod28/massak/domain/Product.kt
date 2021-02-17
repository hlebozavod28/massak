package ru.hlebozavod28.massak.domain

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name = "product", schema = "baking")
class Product {
    @Id
    @GeneratedValue
    var id: Long = 0
    var placeid = 0
    var name: String? = null
    var weightdough: BigDecimal = 0.toBigDecimal()
    var weight: BigDecimal? = null
    var sheetalloc = 0
    var doughid = 0
    var fillingid = 0
    var yieldprod: BigDecimal? = null
    var timeprepline = 0
    var timeprepdoughmin = 0
    var timeprepdoughmax = 0
    var timecooler = 0
    var timetolist = 0

    @Column(name = "\"OUTPUT\"")
    var OUTPUT: BigDecimal? = null
    var timeproofmin = 0
    var timeproofmax = 0
    var timebakingmin = 0
    var timebakingmax = 0
    var ovenoutput: BigDecimal? = null
    var timecooling = 0
    var packspeed: BigDecimal? = null
}