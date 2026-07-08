package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.MakerReference;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MakerReferenceUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // MakerReference mkrRefReference : @NotBlank
    @Test
    public void validMakerReferenceWithMkrRefReferenceNull_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setMkrRefReference(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefReferenceBlankSpace_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setMkrRefReference("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefReferenceBlank_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setMkrRefReference("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefReferenceNotBlank_MustBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setMkrRefReference("REF001");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "mkrRefReference",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }
}
