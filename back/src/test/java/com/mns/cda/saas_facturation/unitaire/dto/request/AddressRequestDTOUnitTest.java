package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AddressRequestDTOUnitTest {

    private static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // AddressRequestDTO pCodeId : @NotNull

    @Test
    public void validAddressWithPCodeIdNull_MustNotBeValidated() {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                null,
                1L
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "pCodeId",
                "NotNull"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validAddressWithPCodeIdNotNull_MustBeValidated() {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                1L,
                1L
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "pCodeId",
                "NotNull"
        );

        Assertions.assertFalse(constraintViolation);
    }

    // AddressRequestDTO cityId : @NotNull

    @Test
    public void validAddressWithCityIdNull_MustNotBeValidated() {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                1L,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "cityId",
                "NotNull"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validAddressWithCityIdNotNull_MustBeValidated() {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                1L,
                1L
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "cityId",
                "NotNull"
        );

        Assertions.assertFalse(constraintViolation);
    }
}