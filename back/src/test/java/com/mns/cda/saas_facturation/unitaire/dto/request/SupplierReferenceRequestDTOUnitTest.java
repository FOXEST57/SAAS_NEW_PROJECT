package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.product.DTO.requestDTO.SupplierReferenceRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class SupplierReferenceRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur articleId (@NotNull)
    @Test
    public void validSupplierReferenceWithArticleNotNull_MustBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                "Référence",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "articleId",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validSupplierReferenceWithArticleNull_MustNotBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                null,
                2L,
                "Référence",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "articleId",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur supplierId (@NotNull)
    @Test
    public void validSupplierReferenceWithSupplierNotNull_MustBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                "Référence",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "supplierId",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validSupplierReferenceWithSupplierNull_MustNotBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                null,
                "Référence",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "supplierId",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur SplRefReference (@NotBlank)
    @Test
    public void validSupplierReferenceWithSplRefReferenceNotBlank_MustBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                "Référence",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceNull_MustNotBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                null,
                BigDecimal.valueOf(5.5),
                3
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceEmpty_MustNotBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                "",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceWithOnlySpace_MustNotBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                " ",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "splRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }
    
    // Tests sur splRefSellPrice (@NotNull)
    @Test
    public void validSupplierReferenceWithSupplierPriceNotNull_MustBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                "Référence",
                BigDecimal.valueOf(5.5),
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "splRefSellPrice",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validSupplierReferenceWithSupplierPriceNull_MustNotBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                "Référence",
                null,
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "splRefSellPrice",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }

    @Test
    public void validSupplierReferenceWithSupplierPriceNegative_MustNotBeValidated() {
        SupplierReferenceRequestDTO supplierReferenceRequestDTO = new SupplierReferenceRequestDTO(
                1L,
                2L,
                "Référence",
                BigDecimal.valueOf(-1),
                3
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReferenceRequestDTO),
                "splRefSellPrice",
                "DecimalMin"
        );

        Assertions.assertTrue(contraintExists);
    }

}
