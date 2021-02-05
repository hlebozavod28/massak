package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "baking_sheets", schema = "\"Tablet\"")
public class HandcardSheets {
    @Id
    @GeneratedValue
    int id;
    long handcart;
    int sheets;
}
