package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.DTO.requestDTO.PostalCodeCityRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PostalCodeCityRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // PostalCodeCityRequestDTO pCodeId : @NotNull
    @Test
    public void validPostalCodeCityRequestDTOWithPCodeIdNull_MustNotBeValidated() {

        PostalCodeCityRequestDTO postalCodeCityRequestDTO = new PostalCodeCityRequestDTO(
                null,
                1L
        );;
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeCityRequestDTO),
                "pCodeId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validPostalCodeCityRequestDTOWithPCodeIdNotNull_MustBeValidated() {

        PostalCodeCityRequestDTO postalCodeCityRequestDTO = new PostalCodeCityRequestDTO(
                1L,
                1L
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeCityRequestDTO),
                "pCodeId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    // PostalCodeCityRequestDTO cityId : @NotNull
    @Test
    public void validPostalCodeCityRequestDTOWithCityIdNull_MustNotBeValidated() {

        PostalCodeCityRequestDTO postalCodeCityRequestDTO = new PostalCodeCityRequestDTO(
                1L,
                null
        );;
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeCityRequestDTO),
                "cityId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validPostalCodeCityRequestDTOWithCityIdNotNull_MustBeValidated() {

        PostalCodeCityRequestDTO postalCodeCityRequestDTO = new PostalCodeCityRequestDTO(
                1L,
                1L
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(postalCodeCityRequestDTO),
                "cityId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }
}
