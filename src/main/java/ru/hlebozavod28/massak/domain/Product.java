package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "product", schema = "baking")
public class Product {
    @Id
    int id;
    @Column(name = "place_id")
    int placeId;
    @Column(name = "prod_name")
    String prodName;
    @Column(name = "weight_dough")
    BigDecimal weightDough;
    @Column(name = "weight")
    BigDecimal weight;
    @Column(name = "sheet_alloc")
    int sheetAlloc;
    @Column(name = "dough_id")
    int doughId;
    @Column(name = "filling_id")
    int fillingId;
    @Column(name = "yield_prod")
    BigDecimal yieldProd;
    @Column(name = "time_prep_line")
    int timePrepLine;
    @Column(name = "time_prep_dough_min")
    int timePrepDoughMin;
    @Column(name = "time_prep_dough_max")
    int timePrepDoughMax;
    @Column(name = "time_cooler")
    int timeCooler;
    @Column(name = "time_to_list")
    int timeToList;
    @Column(name = "proc_output")
    BigDecimal procOutput;
    @Column(name = "time_proof_min")
    int timeProofmin;
    @Column(name = "time_proof_max")
    int timeProofMax;
    @Column(name = "time_baking_min")
    int timeBakingMin;
    @Column(name = "time_baking_max")
    int timeBakingMax;
    @Column(name = "oven_output")
    BigDecimal ovenOutput;
    @Column(name = "time_cooling")
    int timeCooling;
    @Column(name = "pack_speed")
    BigDecimal packSpeed;

}
