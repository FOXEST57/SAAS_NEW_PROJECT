package com.mns.cda.saas_facturation.unitaire.dto.update;

import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateTvaTauxDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class UpdateTvaTauxDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur tvaTaux (@DecimalMin(value = "0.0"))
    @Test
    public void validTvaWithTauxPositive_MustBeValidated() {
        UpdateTvaTauxDTO updateTvaTauxDTO = new UpdateTvaTauxDTO(
                BigDecimal.valueOf(1)
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateTvaTauxDTO),
                "tvaTaux",
                "DecimalMin"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validTvaWithTauxNegative_MustNotBeValidated() {
        UpdateTvaTauxDTO updateTvaTauxDTO = new UpdateTvaTauxDTO(
                BigDecimal.valueOf(-1)
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateTvaTauxDTO),
                "tvaTaux",
                "DecimalMin"
        );

        Assertions.assertTrue(contraintExists);
    }

}
