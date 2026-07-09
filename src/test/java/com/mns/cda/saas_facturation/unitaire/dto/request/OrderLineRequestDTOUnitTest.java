package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.DTO.requestDTO.OrderLineRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OrderLineRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // OrderLineRequestDTO cartId : @NotNull
    @Test
    public void validOrderLineRequestDTOWithCartIdNull_MustNotBeValidated() {

        OrderLineRequestDTO orderLineRequestDTO = new OrderLineRequestDTO(
                null,
                1L,
                2
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(orderLineRequestDTO),
                "cartId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validOrderLineRequestDTOWithCartIdNotNull_MustBeValidated() {

        OrderLineRequestDTO orderLineRequestDTO = new OrderLineRequestDTO(
                1L,
                1L,
                2
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(orderLineRequestDTO),
                "cartId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }
    // OrderLineRequestDTO articleId : @NotNull
    @Test
    public void validOrderLineRequestDTOWithArticleIdNull_MustNotBeValidated() {

        OrderLineRequestDTO orderLineRequestDTO = new OrderLineRequestDTO(
                1L,
                null,
                2
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(orderLineRequestDTO),
                "articleId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validOrderLineRequestDTOWithArticleIdNotNull_MustBeValidated() {

        OrderLineRequestDTO orderLineRequestDTO = new OrderLineRequestDTO(
                1L,
                1L,
                2
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(orderLineRequestDTO),
                "articleId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }
}
