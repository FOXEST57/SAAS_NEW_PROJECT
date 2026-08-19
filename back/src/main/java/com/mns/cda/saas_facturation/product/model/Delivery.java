package com.mns.cda.saas_facturation.product.model;

import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long dlvId;

    @Min(1)
    protected int dlvQuantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    protected BigDecimal dlvBuyingPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected DeliveryStatus dlvStatus;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime dlvCreatedDate;

    @LastModifiedDate
    protected LocalDateTime dlvUpdatedDate;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "spl_article_id", referencedColumnName = "article_id"),
            @JoinColumn(name = "supplier_id", referencedColumnName = "supplier_id")
    })
    protected SupplierReference supplierReference;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "mkr_article_id", referencedColumnName = "article_id"),
            @JoinColumn(name = "maker_id", referencedColumnName = "maker_id")
    })
    protected MakerReference makerReference;

}