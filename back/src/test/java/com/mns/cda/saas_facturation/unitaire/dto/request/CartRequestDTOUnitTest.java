package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.cart.DTO.requestDTO.CartRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CartRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // CartRequestDTO crtRef : @NotBlank
    @Test
    public void validCartWithCrtRefNull_MustNotBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                null,
                "VALIDE",
                1L,
                List.of()

        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtRefBlankSpace_MustNotBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                " ",
                "VALIDE",
                1L,
                List.of()
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtRefBlank_MustNotBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                "",
                "VALIDE",
                1L,
                List.of()
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtRefNotBlank_MustBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                "ELECTRONICS",
                "VALIDE",
                1L,
                List.of()
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    // CartRequestDTO crtStatus : @NotBlank
    @Test
    public void validCartWithCrtStatusNull_MustNotBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                "ELECTRONICS",
                null,
                1L,
                List.of()
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtStatusBlankSpace_MustNotBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                "ELECTRONICS",
                " ",
                1L,
                List.of()
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtStatusBlank_MustNotBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                "ELECTRONICS",
                "",
                1L,
                List.of()
        );


        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtStatusNotBlank_MustBeValidated() {

        CartRequestDTO cart = new CartRequestDTO(
                "ELECTRONICS",
                "ACTIVE",
                1L,
                List.of()
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }
}
