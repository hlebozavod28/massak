package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "product", schema = "baking")
public class Product {
    @Id
    @GeneratedValue
    long id;
    int placeid;
    String name;
    BigDecimal weightdough;
    BigDecimal weight;
    int sheetalloc;
    int doughid;
    int fillingid;
    BigDecimal yieldprod;
    int timeprepline;
    int timeprepdoughmin;
    int timeprepdoughmax;
    int timecooler;
    int timetolist;
    @Column(name = "\"OUTPUT\"")
    BigDecimal OUTPUT;
    int timeproofmin;
    int timeproofmax;
    int timebakingmin;
    int timebakingmax;
    BigDecimal ovenoutput;
    int timecooling;
    BigDecimal packspeed;
}
