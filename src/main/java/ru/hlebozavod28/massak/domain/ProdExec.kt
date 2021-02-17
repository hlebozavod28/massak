package ru.hlebozavod28.massak.domain

import javax.persistence.GeneratedValue
import java.math.BigDecimal
import java.sql.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "prodexec", schema = "baking")
class ProdExec {
    @Id
    @GeneratedValue
    var id = 0
    var prod_date: Date? = null
    var prod_smena = 0
    var prod_id = 0
    var number_line_kg: BigDecimal? = null
    var number_line_items = 0
    var number_proof_kg: BigDecimal? = null
    var number_proof_items = 0
    var number_baking_kg: BigDecimal? = null
    var number_baking_items = 0
    var number_cool_kg: BigDecimal? = null
    var number_cool_items = 0
    var number_pack_kg: BigDecimal? = null
    var number_pack_items = 0
}