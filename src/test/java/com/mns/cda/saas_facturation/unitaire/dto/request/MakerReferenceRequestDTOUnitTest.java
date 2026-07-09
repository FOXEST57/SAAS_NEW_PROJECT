package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
                "REF123"
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
                "REF123"
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
                "REF123"
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
                "REF123"
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "mkrId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    // MakerReferenceRequestDTO mkrRefReference : @NotBlank
    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceNull_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceBlankSpace_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "  "
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceBlank_MustNotBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                ""
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceRequestDTOWithMkrRefReferenceNotBlank_MustBeValidated() {

        MakerReferenceRequestDTO makerReferenceRequestDTO = new MakerReferenceRequestDTO(
                1L,
                1L,
                "REF123"
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReferenceRequestDTO),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }
}
