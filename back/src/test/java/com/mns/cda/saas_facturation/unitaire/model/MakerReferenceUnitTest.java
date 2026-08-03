package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.product.model.MakerReference;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MakerReferenceUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // MakerReference artMkrReference : @NotBlank
    @Test
    public void validMakerReferenceWithMkrRefReferenceNull_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setArtMkrReference(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefReferenceBlankSpace_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setArtMkrReference("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefReferenceBlank_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setArtMkrReference("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefReferenceNotBlank_MustBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setArtMkrReference("REF001");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrReference",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefSellPriceNotNull_MustBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setArtMkrSellPrice(BigDecimal.valueOf(1.0));

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrSellPrice",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefSellPriceNull_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrSellPrice",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefSellPriceNegative_MustNotBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setArtMkrSellPrice(BigDecimal.valueOf(-1));

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrSellPrice",
                "DecimalMin"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerReferenceWithMkrRefSellPricePositive_MustBeValidated() {

        MakerReference makerReference = new MakerReference();
        makerReference.setArtMkrSellPrice(BigDecimal.valueOf(1));

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(makerReference),
                "artMkrSellPrice",
                "DecimalMin"
        );
        Assertions.assertFalse(constraintExist);
    }
}
