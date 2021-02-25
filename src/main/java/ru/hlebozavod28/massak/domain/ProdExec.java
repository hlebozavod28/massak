package ru.hlebozavod28.massak.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "prodexec", schema = "baking")
public class ProdExec {
    @Id
    @GeneratedValue
    int id;
    @Column(name = "prod_date")
    LocalDate prodDate;
    @Column(name = "prod_smena" )
    int prodSmena;
    @Column(name = "prod_id")
    int prodId;
    @Column(name = "number_line_kg")
    BigDecimal numberLineKg;
    @Column(name = "number_line_items")
    int numberLineItems;
    @Column(name = "number_proof_kg")
    BigDecimal numberProofKg;
    @Column(name = "number_proof_items")
    int numberProofItems;
    @Column(name = "number_baking_kg")
    BigDecimal numberBakingKg;
    @Column(name = "number_baking_items")
    int numberBakingItems;
    @Column(name = "number_cool_kg")
    BigDecimal numberCoolKg;
    @Column(name = "number_cool_items")
    int numberCoolItems;
    @Column(name = "number_pack_kg")
    BigDecimal numberPackKg;
    @Column(name = "number_pack_items")
    int numberPackItems;

    public ProdExec(LocalDate prod_date, int prod_smena, int prod_id) {
        this.prodDate = prod_date;
        this.prodSmena = prod_smena;
        this.prodId = prod_id;
        this.numberLineItems = 0;
    }
}
