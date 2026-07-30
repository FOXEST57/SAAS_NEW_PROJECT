package com.mns.cda.saas_facturation.model;

import com.mns.cda.saas_facturation.config.LowercaseConverter;
import com.mns.cda.saas_facturation.enumeration.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class MakerReference {

    @Embeddable
    @Setter
    @Getter
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

    @Column(unique = true, nullable = false)
    @NotBlank
    @Convert(converter = LowercaseConverter.class)
    protected  String artMkrReference;

    protected int artMkrStock;

    @CreatedDate
    protected LocalDateTime artMrkCreateDate;

    @LastModifiedDate
    protected LocalDateTime artMrkUpdateDate;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    protected BigDecimal artMkrSellPrice;

    @NotBlank
    @Enumerated(EnumType.STRING)
    protected DeliveryStatus status;

}
