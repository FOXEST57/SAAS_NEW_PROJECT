package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class OrderId implements Serializable {
        @Column(name = "article_id")
        private Long articleId;
        @Column(name = "cart_id")
        private Long makerId;
    }

    @EmbeddedId
    private OrderId ordId;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    protected Article article;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "cart_id")
    protected Maker maker;

    @Column(nullable = false)
    @NotBlank
    protected String ordQuantity;
}