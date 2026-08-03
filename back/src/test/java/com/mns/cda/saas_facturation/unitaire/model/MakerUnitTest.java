package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.location.model.Address;
import com.mns.cda.saas_facturation.product.model.Maker;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MakerUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Maker mkrName : @NotBlank
    @Test
    public void validMakerWithMkrNameNull_MustNotBeValidated() {

        Maker maker = new Maker();
        maker.setMkrName(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validMakerWithMkrNameNotNull_MustNotBeValidated() {

        Maker maker = new Maker();
        maker.setMkrName("Lenovo");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    // Tests sur le nom de fabricant (@NotBlank)
    @Test
    public void validMakerWithNameNotBlank_MustBeValidated() {
        Maker maker = new Maker();
        maker.setMkrName("Nom");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validMakerWithNameNull_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrName(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validMakerWithNameEmpty_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrName("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validMakerWithNameWithOnlySpace_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrName(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrName",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur l'adresse email du fabricant (@Email, @NotBlank)
    // Tests sur @Email
    @Test
    public void validMakerWithEmailCorrectFormat_MustBeValidated() {
        Maker maker = new Maker();
        maker.setMkrEmail("a@a.com");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrEmail",
                "Email"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validMakerWithEmailIncorrectFormat_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrEmail("a.a.com");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrEmail",
                "Email"
        );

        Assertions.assertTrue(constraintExists);
    }

    // Tests sur @NotBlank
    @Test
    public void validMakerWithEmailNotBlank_MustBeValidated() {
        Maker maker = new Maker();
        maker.setMkrEmail("Email");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrEmail",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validMakerWithEmailNull_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrEmail(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrEmail",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validMakerWithEmailEmpty_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrEmail("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrEmail",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validMakerWithEmailWithOnlySpace_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrEmail(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrEmail",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }


    // Tests sur le numéro de téléphone du fabricant (@NotBlank, @ValidPhoneNumber)
    // Tests sur @NotBlank
    @Test
    public void validMakerWithPhoneNotBlank_MustBeValidated() {
        Maker maker = new Maker();
        maker.setMkrPhone("Phone");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrPhone",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validMakerWithPhoneNull_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrPhone(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrPhone",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validMakerWithPhoneEmpty_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrPhone("");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrPhone",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validMakerWithPhoneWithOnlySpace_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrPhone(" ");

        boolean contraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrPhone",
                "NotBlank"
        );

        Assertions.assertTrue(contraintExists);
    }

    // Test sur @ValidPhoneNumber
    @Test
    public void validMakerWithPhoneCorrectFormat_MustBeValidated() {
        Maker maker = new Maker();
        maker.setMkrPhone("+33758664154");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrPhone",
                "ValidPhoneNumber"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validMakerWithPhoneWithoutPrefix_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrPhone("0758664154");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrPhone",
                "ValidPhoneNumber"
        );

        Assertions.assertTrue(constraintExists);
    }

    @Test
    public void validMakerWithPhoneWithIncorrectLength_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setMkrPhone("+337558664154");

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "mkrPhone",
                "ValidPhoneNumber"
        );

        Assertions.assertTrue(constraintExists);
    }


    // Tests sur l'adresse du fabricant (@NotNull)
    @Test
    public void validMakerWithAddressNotNull_MustBeValidated() {
        Maker maker = new Maker();
        maker.setAddress(new Address());

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "address",
                "NotNull"
        );

        Assertions.assertFalse(constraintExists);
    }

    @Test
    public void validMakerWithAddressNull_MustNotBeValidated() {
        Maker maker = new Maker();
        maker.setAddress(null);

        boolean constraintExists = TestUtilitaire.constraintViolationExist(
                validator.validate(maker),
                "address",
                "NotNull"
        );

        Assertions.assertTrue(constraintExists);
    }

}
