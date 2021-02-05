package ru.hlebozavod28.massak.domain;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "workline_product", schema = "workplaces")
public class WorklineProduct {
    @Id
    @GeneratedValue
    long id;
    @Column(name = "workline_id", nullable = false)
    long worklineId;
    @Column(name = "product_code")
    int productCode;
    @Column(name = "product_start")
    Timestamp productStart;
}
