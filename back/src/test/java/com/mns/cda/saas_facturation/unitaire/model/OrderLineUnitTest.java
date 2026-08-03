package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.cart.model.OrderLine;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OrderLineUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // OrderLine ordLnQuantity : @NotNull
    @Test
    public void validOrderLineWithOrdLnQuantityNull_MustNotBeValidated() {

        OrderLine orderLine = new OrderLine();
        orderLine.setOrdLnQuantity(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(orderLine),
                "ordLnQuantity",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validOrderLineWithOrdLnQuantityZero_MustBeValidated() {

        OrderLine orderLine = new OrderLine();
        orderLine.setOrdLnQuantity(0);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(orderLine),
                "ordLnQuantity",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validOrderLineWithOrdLnQuantityPositive_MustBeValidated() {

        OrderLine orderLine = new OrderLine();
        orderLine.setOrdLnQuantity(5);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(orderLine),
                "ordLnQuantity",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

}
