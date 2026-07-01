package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SupplierReference {

    @Embeddable
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
    protected Article article;


    @ManyToOne
    @MapsId("supplierId")
    @JoinColumn(name = "supplier_id")
    protected Supplier supplier;

    @NotBlank
    protected String SplRefReference;

    @NotNull
    protected int SplRefStock;

}
