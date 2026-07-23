package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SupplierRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Tests sur le nom de fournisseur (@NotBlank)
    @Test
    public void validSupplierWithNameNotBlank_MustBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "name",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithNameNull_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                null,
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "name",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithNameEmpty_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "",
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "name",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithNameWithOnlySpace_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                " ",
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "name",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur l'adresse email du fournisseur (@Email, @NotBlank)
        // Tests sur @Email
    @Test
    public void validSupplierWithEmailCorrectFormat_MustBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "email",
                "Email"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithEmailIncorrectFormat_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a.a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "email",
                "Email"
        );

        Assertions.assertTrue(constraintExists);
    }

        // Tests sur @NotBlank
        @Test
        public void validSupplierWithEmailNotBlank_MustBeValidated() {
            SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                    "Nom",
                    "a@a.com",
                    "+33458655192",
                    2L
            );

            boolean constraintExists = TestUtilitaire.constraintViolationExist(
                    validator.validate(supplierRequestDTO),
                    "email",
                    "NotBlank"
            );

            Assertions.assertFalse(constraintExists);
        }

    @Test
    public void validSupplierWithEmailNull_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                null,
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "email",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithEmailEmpty_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "email",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithEmailWithOnlySpace_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                " ",
                "+33458655192",
                2L
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "email",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur le numéro de téléphone du fournisseur (@NotBlank, @ValidPhoneNumber)
        // Tests sur @NotBlank
    @Test
    public void validSupplierWithPhoneNotBlank_MustBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "phoneNumber",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneNull_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                null,
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "phoneNumber",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneEmpty_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "phoneNumber",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneWithOnlySpace_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                " ",
                2L
        );

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "phoneNumber",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }

        // Test sur @ValidPhoneNumber
    @Test
    public void validSupplierWithPhoneCorrectFormat_MustBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "phoneNumber",
                "ValidPhoneNumber"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneWithoutPrefix_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "0458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "phoneNumber",
                "ValidPhoneNumber"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validSupplierWithPhoneWithIncorrectLength_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "+3345865519287",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "phoneNumber",
                "ValidPhoneNumber"
        );

        Assertions.assertTrue(constraintExists);
    }


    // Tests sur l'adresse du fournisseur (@NotNull)
    @Test
    public void validSupplierWithAddressNotNull_MustBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "+33458655192",
                2L
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "addressId",
                "NotNull"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validSupplierWithAddressNull_MustNotBeValidated() {
        SupplierRequestDTO supplierRequestDTO = new SupplierRequestDTO(
                "Nom",
                "a@a.com",
                "+33458655192",
                null
        );

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(supplierRequestDTO),
                "addressId",
                "NotNull"
        );

        Assertions.assertTrue(constraintExists);
    }

}