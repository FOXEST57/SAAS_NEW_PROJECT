package com.mns.cda.saas_facturation.product.model;

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
import java.util.ArrayList;
import java.util.List;

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
    @NotNull
    protected Article article;

    @ManyToOne
    @MapsId("makerId")
    @JoinColumn(name ="maker_id")
    @NotNull
    protected Maker maker;

    @OneToMany(mappedBy = "makerReference")
    @NotNull
    protected List<Delivery> deliveries = new ArrayList<>();

    @Column(unique = true, nullable = false)
    @NotBlank
    @Convert(converter = LowercaseConverter.class)
    protected  String artMkrReference;

}
