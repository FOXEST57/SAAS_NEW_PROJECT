package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.user.DTO.requestDTO.AccountTypeRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AccountTypeRequestUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // AccountTypeRequest accTypeLibelle : @NotBlank
    @Test
    public void validAccountTypeWithAccTypeLibelleNull_MustNotBeValidated() {

        AccountTypeRequestDTO AccountTypeRequest = new AccountTypeRequestDTO(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountTypeRequest),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }
    @Test
    public void validAccountTypeWithAccTypeLibelleBlank_MustNotBeValidated() {

        AccountTypeRequestDTO AccountTypeRequest = new AccountTypeRequestDTO("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountTypeRequest),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validAccountTypeWithAccTypeLibelleWithSpace_MustNotBeValidated() {

        AccountTypeRequestDTO AccountTypeRequest = new AccountTypeRequestDTO("   ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountTypeRequest),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validAccountTypeWithAccTypeLibelleNotNull_MustNotBeValidated() {

        AccountTypeRequestDTO AccountTypeRequest = new AccountTypeRequestDTO("Client");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(AccountTypeRequest),
                "accTypeLibelle",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }
}
