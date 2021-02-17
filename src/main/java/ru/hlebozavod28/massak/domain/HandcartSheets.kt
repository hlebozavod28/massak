package ru.hlebozavod28.massak.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "baking_sheets", schema = "\"Tablet\"")
public class HandcardSheets {
    @Id
    @GeneratedValue
    int id;
    long handcart;
    int sheets;

    public HandcardSheets(int sheets) {
        this.sheets = sheets;
    }
}
