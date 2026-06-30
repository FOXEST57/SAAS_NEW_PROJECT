package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MakerReference {

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class MakerReferenceId implements Serializable {
        @Column(name ="article_id")
        private Long articleId;
        @Column(name ="maker_id")
        private Long makerId;
    }

    @EmbeddedId
    private MakerReferenceId mkrRefId;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    protected Article article;

    @ManyToOne
    @MapsId("makerId")
    @JoinColumn(name ="maker_id")
    protected Maker maker;

    @NotBlank
    protected  String mkrRefReference;

    @NotNull
    protected Long mkrRefStock;

}
