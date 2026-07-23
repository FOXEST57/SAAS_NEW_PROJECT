package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.Supplier;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SupplierUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur le nom de fournisseur (@NotBlank)
    @Test
    public void validSupplierWithNameNotBlank_MustBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplName("Nom");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithNameNull_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplName(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithNameEmpty_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplName("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithNameWithOnlySpace_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplName(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur l'adresse email du fournisseur (@Email, @NotBlank)
        // Tests sur @Email
    @Test
    public void validSupplierWithEmailCorrectFormat_MustBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplEmail("a@a.com");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splEmail",
                "Email"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithEmailIncorrectFormat_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplEmail("a.a.com");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splEmail",
                "Email"
        );

        Assertions.assertTrue(constraintExists);
    }

        // Tests sur @NotBlank
        @Test
        public void validSupplierWithEmailNotBlank_MustBeValidated() {
            Supplier supplier = new Supplier();
            supplier.setSplEmail("Email");

            boolean constraintExists = TestUtilitaire.constraintViolationExist(
                    validator.validate(supplier),
                    "splEmail",
                    "NotBlank"
            );

            Assertions.assertFalse(constraintExists);
        }

    @Test
    public void validSupplierWithEmailNull_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplEmail(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splEmail",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithEmailEmpty_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplEmail("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splEmail",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithEmailWithOnlySpace_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplEmail(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splEmail",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur le numéro de téléphone du fournisseur (@NotBlank, @ValidPhoneNumber)
        // Tests sur @NotBlank
    @Test
    public void validSupplierWithPhoneNotBlank_MustBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplPhone("Phone");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splPhone",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneNull_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplPhone(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splPhone",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneEmpty_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplPhone("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splPhone",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneWithOnlySpace_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplPhone(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splPhone",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }

        // Test sur @ValidPhoneNumber
    @Test
    public void validSupplierWithPhoneCorrectFormat_MustBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplPhone("+33758664154");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splPhone",
                "ValidPhoneNumber"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneWithoutPrefix_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplPhone("0758664154");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splPhone",
                "ValidPhoneNumber"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneWithIncorrectLength_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setSplPhone("+337558664154");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "splPhone",
                "ValidPhoneNumber"
        );

        Assertions.assertTrue(constraintExists);
    }


    // Tests sur l'adresse du fournisseur (@NotNull)
    @Test
    public void validSupplierWithAddressNotNull_MustBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setAddress(new Address());

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "address",
                "NotNull"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithAddressNull_MustNotBeValidated() {
        Supplier supplier = new Supplier();
        supplier.setAddress(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplier),
                "address",
                "NotNull"
        );

        Assertions.assertTrue(constraintExists);
    }

}
