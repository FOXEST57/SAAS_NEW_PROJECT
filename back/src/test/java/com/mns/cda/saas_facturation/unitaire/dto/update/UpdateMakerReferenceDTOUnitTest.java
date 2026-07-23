package com.mns.cda.saas_facturation.unitaire.dto.update;

import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class UpdateMakerReferenceDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // UpdateMakerReferenceDTO artMkrReference : @NotBlank
    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceNull_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                null,
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceBlankSpace_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "  ",
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceBlank_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "",
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceNotBlank_MustBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "REF123",
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithArtMkrSellPriceNegative_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "REF123",
                0,
                BigDecimal.valueOf(-1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "artMkrSellPrice",
                "DecimalMin"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithArtMkrSellPricePositive_MustBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "REF123",
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "artMkrSellPrice",
                "DecimalMin"
        );
        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithArtMkrSellPriceNull_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "REF123",
                0,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "artMkrSellPrice",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }
}
