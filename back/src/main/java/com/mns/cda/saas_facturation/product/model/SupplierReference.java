package com.mns.cda.saas_facturation.product.model;

import com.mns.cda.saas_facturation.config.LowercaseConverter;
import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SupplierReference {

    @Embeddable
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SupplierReferenceId implements Serializable {
        @Column(name ="article_id")
        Long articleId;
        @Column(name = "supplier_id")
        Long supplierId;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            SupplierReferenceId that = (SupplierReferenceId) o;
            return Objects.equals(articleId, that.articleId) && Objects.equals(supplierId, that.supplierId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(articleId, supplierId);
        }
    }

    @EmbeddedId
    private SupplierReferenceId splRefId;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    @NotNull
    protected Article article;


    @ManyToOne
    @MapsId("supplierId")
    @JoinColumn(name = "supplier_id")
    @NotNull
    protected Supplier supplier;

    @NotBlank
    @Column(unique = true)
    @Convert(converter = LowercaseConverter.class)
    protected String splRefReference;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    protected BigDecimal splRefSellPrice;

    protected int splRefStock;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime splRefCreateDate;

    @LastModifiedDate
    protected LocalDateTime splRefUpdateDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected DeliveryStatus status;

}
