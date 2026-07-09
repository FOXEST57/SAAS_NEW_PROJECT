package com.mns.cda.saas_facturation.unitaire.dto.update;

import com.mns.cda.saas_facturation.DTO.requestDTO.MakerReferenceRequestDTO;
import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateOrderLineDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UpdateOrderLineDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // UpdateOrderLineDTOUnitTest quantity : @NotNull
    @Test
    public void validUpdateOrderLineDTOWithQuantityNull_MustNotBeValidated() {

        UpdateOrderLineDTO updateOrderLineDTO = new UpdateOrderLineDTO(
                null
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateOrderLineDTO),
                "quantity",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateOrderLineDTOWithQuantityNotNull_MustBeValidated() {

        UpdateOrderLineDTO updateOrderLineDTO = new UpdateOrderLineDTO(
                5
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateOrderLineDTO),
                "quantity",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    //UpdateOrderLineDTO quantity : @Min(1)
    @Test
    public void validUpdateOrderLineDTOWithQuantityLessThan1_MustNotBeValidated() {

        UpdateOrderLineDTO updateOrderLineDTO = new UpdateOrderLineDTO(
                0
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateOrderLineDTO),
                "quantity",
                "Min"
        );
        Assertions.assertTrue(constraintExist);
    }
    @Test
    public void validUpdateOrderLineDTOWithQuantityGreaterThan1_MustBeValidated() {

        UpdateOrderLineDTO updateOrderLineDTO = new UpdateOrderLineDTO(
                2
        );
        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateOrderLineDTO),
                "quantity",
                "Min"
        );
        Assertions.assertFalse(constraintExist);
    }

}
