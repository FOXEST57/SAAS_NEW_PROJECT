package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.cart.model.Cart;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CartUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Cart crtRef : @NotBlank
    @Test
    public void validCartWithCrtRefNull_MustNotBeValidated() {

        Cart cart = new Cart();
        cart.setCrtRef(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtRefBlankSpace_MustNotBeValidated() {

        Cart cart = new Cart();
        cart.setCrtRef("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtRefBlank_MustNotBeValidated() {

        Cart cart = new Cart();
        cart.setCrtRef("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtRefNotBlank_MustBeValidated() {

        Cart cart = new Cart();
        cart.setCrtRef("ELECTRONICS");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtRef",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    // Cart crtStatus : @NotBlank
    @Test
    public void validCartWithCrtStatusNull_MustNotBeValidated() {

        Cart cart = new Cart();
        cart.setCrtStatus(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtStatusBlankSpace_MustNotBeValidated() {

        Cart cart = new Cart();
        cart.setCrtStatus("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtStatusBlank_MustNotBeValidated() {

        Cart cart = new Cart();
        cart.setCrtStatus("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCartWithCrtStatusNotBlank_MustBeValidated() {

        Cart cart = new Cart();
        cart.setCrtStatus("ACTIVE");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(cart),
                "crtStatus",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }
}
