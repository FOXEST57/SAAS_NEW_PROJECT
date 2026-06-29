package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ArticleSupplier {

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArticleSupplierId implements Serializable {
        @Column(name ="article_id")
        private Long articleId;
        @Column(name = "supplier_id")
        private Long supplierId;
    }

    @EmbeddedId
    private ArticleSupplierId artSplId;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    protected Article article;

    @ManyToOne
    @MapsId("supplierId")
    @JoinColumn(name = "supplier_id")
    protected Supplier supplier;

    @NotBlank
    protected String artSplReference;

    @NotNull
    protected int artSplStock;

}
