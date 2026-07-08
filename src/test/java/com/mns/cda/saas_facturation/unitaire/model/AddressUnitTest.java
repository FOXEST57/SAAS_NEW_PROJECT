package com.mns.cda.saas_facturation.unitaire.model;


import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.PostalCode;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class AddressUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }


    @Test
    public void validAddressWithPostalCodeNull_MustNotBeValidated() {

        Address address = new Address();
        address.setPostalCode(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "postalCode",
                "NotNull"
        );

        Assertions.assertTrue(constraintExist);

    }
    @Test
    public void validAddressWithPostalCodeNotNull_MustBeValidated() {

        Address address = new Address();
        address.setPostalCode(new PostalCode());

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(address),
                "postalCode",
                "NotNull"
        );

        Assertions.assertFalse(constraintExist);

    }
}
