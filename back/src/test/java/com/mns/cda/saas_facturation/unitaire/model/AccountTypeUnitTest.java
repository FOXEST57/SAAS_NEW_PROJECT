package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.AccountType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AccountTypeUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // AccountType accTypeLibelle : @NotBlank
    @Test
    public void validAccountTypeWithAccTypeLibelleNull_MustNotBeValidated() {

        AccountType AccountType = new AccountType();
        AccountType.setAccTypeLibelle(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountType),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }
    @Test
    public void validAccountTypeWithAccTypeLibelleBlank_MustNotBeValidated() {

        AccountType AccountType = new AccountType();
        AccountType.setAccTypeLibelle("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountType),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validAccountTypeWithAccTypeLibelleWithSpace_MustNotBeValidated() {

        AccountType AccountType = new AccountType();
        AccountType.setAccTypeLibelle("   ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountType),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validAccountTypeWithAccTypeLibelleNotNull_MustNotBeValidated() {

        AccountType AccountType = new AccountType();
        AccountType.setAccTypeLibelle("Client");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountType),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }
}
