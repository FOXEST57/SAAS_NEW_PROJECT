package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ArticleMaker {

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArticleMakerId implements Serializable {
        @Column(name ="article_id")
        private Long articleId;
        @Column(name ="maker_id")
        private Long makerId;
    }

    @EmbeddedId
    private ArticleMakerId artMkrId;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    protected Article article;

    @ManyToOne
    @MapsId("makerId")
    @JoinColumn(name ="maker_id")
    protected Maker maker;

    @NotBlank
    protected  String artMkrReference;

}
