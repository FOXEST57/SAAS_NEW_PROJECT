package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.Article;
import com.mns.cda.saas_facturation.model.Supplier;
import com.mns.cda.saas_facturation.model.SupplierReference;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class SupplierReferenceUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur article (@NotNull)
    @Test
    public void validSupplierReferenceWithArticleNotNull_MustBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setArticle(new Article());

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "article",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validSupplierReferenceWithArticleNull_MustNotBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setArticle(null);

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "article",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur supplier (@NotNull)
    @Test
    public void validSupplierReferenceWithSupplierNotNull_MustBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSupplier(new Supplier());

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "supplier",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validSupplierReferenceWithSupplierNull_MustNotBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSupplier(null);

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "supplier",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur SplRefReference (@NotBlank)
    @Test
    public void validSupplierReferenceWithSplRefReferenceNotBlank_MustBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSplRefReference("Référence");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "SplRefReference",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceNull_MustNotBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSplRefReference(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "SplRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceEmpty_MustNotBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSplRefReference("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "SplRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierReferenceWithSplRefReferenceWithOnlySpace_MustNotBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSplRefReference(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "SplRefReference",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }
    
    // Tests sur supplierPrice (@NotNull)
    @Test
    public void validSupplierReferenceWithSupplierPriceNotNull_MustBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSupplierPrice(new BigDecimal(5));

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "supplierPrice",
                "NotNull"
        );

        Assertions.assertFalse(contraintExists);
    }

    @Test
    public void validSupplierReferenceWithSupplierPriceNull_MustNotBeValidated() {
        SupplierReference supplierReference = new SupplierReference();
        supplierReference.setSupplierPrice(null);

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierReference),
                "supplierPrice",
                "NotNull"
        );

        Assertions.assertTrue(contraintExists);
    }

}
