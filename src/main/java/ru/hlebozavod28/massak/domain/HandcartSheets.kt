package ru.hlebozavod28.massak.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "baking_sheets", schema = "\"Tablet\"")
class HandcartSheets(var sheets: Int) {
    @Id
    @GeneratedValue
    var id = 0
    var handcart: Long = 0
}