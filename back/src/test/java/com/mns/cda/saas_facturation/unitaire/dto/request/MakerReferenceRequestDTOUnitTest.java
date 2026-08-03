package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.product.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MakerReferenceRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // MakerReferenceRequestDTO artId : @NotNull
    @Test
    public void validMakerReferenceRequestDTOWithArtIdNull_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                null,
                1L,
                "REF123",
                0,
                BigDecimal.valueOf(1)
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithArtIdNotNull_MustBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "REF123",
                0,
                BigDecimal.valueOf(1)
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    // MakerReferenceRequestDTO mkrId : @NotNull
    @Test
    public void validMakerReferenceRequestDTOWithMkrIdNull_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                null,
                "REF123",
                0,
                BigDecimal.valueOf(1)
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "mkrId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }
    @Test
    public void validMakerReferenceRequestDTOWithMkrIdNotNull_MustBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "REF123",
                0,
                BigDecimal.valueOf(1)
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "mkrId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    // MakerReferenceRequestDTO artMkrReference : @NotBlank
    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceNull_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                null,
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceBlankSpace_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "  ",
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceBlank_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "",
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceNotBlank_MustBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "REF123",
                0,
                BigDecimal.valueOf(1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithArtMkrSellPriceNull_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "REF123",
                0,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artMkrSellPrice",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithArtMkrSellPriceNegative_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "REF123",
                0,
                BigDecimal.valueOf(-1)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "artMkrSellPrice",
                "DecimalMin"
        );
        Assertions.assertTrue(constraintExist);
    }
}
