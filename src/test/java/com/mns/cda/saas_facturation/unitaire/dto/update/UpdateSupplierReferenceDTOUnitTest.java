package com.mns.cda.saas_facturation.unitaire.dto.update;

import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateSupplierReferenceDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class UpdateSupplierReferenceDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur splRefReference (@NotBlank)
    @Test
    public void validSupplierReferenceWithSplRefReferenceNotBlank_MustBeValidated() {
        UpdateSupplierReferenceDTO updateSupplierReferenceDTO = new UpdateSupplierReferenceDTO(
                "Référence",
                BigDecimal.valueOf(4),
                1
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateSupplierReferenceDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceNull_MustNotBeValidated() {
        UpdateSupplierReferenceDTO updateSupplierReferenceDTO = new UpdateSupplierReferenceDTO(
                null,
                BigDecimal.valueOf(4),
                1
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateSupplierReferenceDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceEmpty_MustNotBeValidated() {
        UpdateSupplierReferenceDTO updateSupplierReferenceDTO = new UpdateSupplierReferenceDTO(
                "",
                BigDecimal.valueOf(4),
                1
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateSupplierReferenceDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceWithOnlySpace_MustNotBeValidated() {
        UpdateSupplierReferenceDTO updateSupplierReferenceDTO = new UpdateSupplierReferenceDTO(
                " ",
                BigDecimal.valueOf(4),
                1
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateSupplierReferenceDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }


    // Tests sur splRefSellPrice (@NotNull)
    @Test
    public void validSupplierReferenceWithSupplierPriceNotNull_MustBeValidated() {
        UpdateSupplierReferenceDTO updateSupplierReferenceDTO = new UpdateSupplierReferenceDTO(
                "Référence",
                BigDecimal.valueOf(4),
                1
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateSupplierReferenceDTO),
                "splRefSellPrice",
                "NotNull"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSupplierPriceNull_MustNotBeValidated() {
        UpdateSupplierReferenceDTO updateSupplierReferenceDTO = new UpdateSupplierReferenceDTO(
                "Référence",
                null,
                1
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateSupplierReferenceDTO),
                "splRefSellPrice",
                "NotNull"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSupplierPriceNegative_MustNotBeValidated() {
        UpdateSupplierReferenceDTO updateSupplierReferenceDTO = new UpdateSupplierReferenceDTO(
                "Référence",
                BigDecimal.valueOf(-1),
                1
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(updateSupplierReferenceDTO),
                "splRefSellPrice",
                "DecimalMin"
        );

        Assertions.assertTrue(constraintExists);
    }

}
