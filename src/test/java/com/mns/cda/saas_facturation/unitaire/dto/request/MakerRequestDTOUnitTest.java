package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.DTO.requestDTO.MakerRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MakerRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // MakerRequestDTO mkrName : @NotBlank
    @Test
    public void validMakerRequestDTOWithMkrNameNull_MustNotBeValidated() {

        MakerRequestDTO makerRequestDTO = new MakerRequestDTO(
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerRequestDTO),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerRequestDTOWithMkrRefReferenceBlankSpace_MustNotBeValidated() {

        MakerRequestDTO makerRequestDTO = new MakerRequestDTO(
                "  "
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerRequestDTO),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerRequestDTOWithMkrRefReferenceBlank_MustNotBeValidated() {

        MakerRequestDTO makerRequestDTO = new MakerRequestDTO(
                ""
        );


        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerRequestDTO),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerRequestDTOWithMkrNameNotBlank_MustBeValidated() {

        MakerRequestDTO makerRequestDTO = new MakerRequestDTO(
                "Valid Maker Name"
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerRequestDTO),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }
}
