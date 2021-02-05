package ru.hlebozavod28.massak.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Table(name = "handcard_motion", schema = "workplaces")
public class Motion {
    @Id
    @GeneratedValue
    long id;
    @Column(name = "handcart_id", nullable = false)
    long handcardId;
    @Column(name = "workplace_id", nullable = false)
    long workplaceId;
    @Column(name = "sub_workplace")
    int subWorkplace;
    @CreationTimestamp
    @Column(name = "in_timestamp", nullable = false, updatable = false)
    Timestamp inTs;
    @UpdateTimestamp
    @Column(name = "out_timestamp")
    Timestamp outTs;
    @Column(name = "product_code")
    int productCode;
    @Column(name = "amount")
    BigDecimal amount;

    public Motion(long workplaceId, long handcardId, int productCode) {
        this.handcardId = handcardId;
        this.workplaceId = workplaceId;
        this.productCode = productCode;
    }
}
