package com.mns.cda.saas_facturation.unitaire.dto;


import com.mns.cda.saas_facturation.DTO.requestDTO.AddressRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class AddressRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // AddressRequestDTO pCodeId: @NotNull
    @Test
    public void validAddressWithPCodeIdNull_MustNotBeValidated() {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                null,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "pCodeId",
                "NotNull"
        );

        Assertions.assertTrue(constraintExist);

    }
    @Test
    public void validAddressWithPCodeIdNotNull_MustBeValidated() {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                1L,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "pCodeId",
                "NotNull"
        );

        Assertions.assertFalse(constraintExist);

    }

    // AddressRequestDTO cityId: @NotNull
    @Test
    public void validAddressWithCityIdNotNull_MustBeValidated() {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                null,
                1L

        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "cityId",
                "NotNull"
        );

        Assertions.assertFalse(constraintExist);

    }

    @Test
    public void validAddressWithCityIdNull_MustNotBeValidated () {

        AddressRequestDTO address = new AddressRequestDTO(
                null,
                null,
                null,
                null,
                null
        );

        boolean contraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "cityId",
                "NotNull"
        );

        Assertions.assertTrue(contraintViolation);
    }
}
