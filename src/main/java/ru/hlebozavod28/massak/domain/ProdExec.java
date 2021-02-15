package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@Entity
@Table(name = "prodexec", schema = "baking")
public class ProdExec {
    @Id
    @GeneratedValue
    int id;
    Date prod_date;
    int prod_smena;
    int prod_id;
    BigDecimal number_line_kg;
    int number_line_items;
    BigDecimal number_proof_kg;
    int number_proof_items;
    BigDecimal number_baking_kg;
    int number_baking_items;
    BigDecimal number_cool_kg;
    int number_cool_items;
    BigDecimal number_pack_kg;
    int number_pack_items;
}
