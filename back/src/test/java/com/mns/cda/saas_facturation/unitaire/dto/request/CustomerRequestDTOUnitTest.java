package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.user.DTO.requestDTO.CustomerRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CustomerRequestDTOUnitTest {
    
    public static Validator validator;
    
    @BeforeAll
    public static void init(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // CustomerRequestDTO ctmFirstName : @NotBlank
    @Test
    public void validCustomerWithCtmFirstNameNull_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                null,
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmFirstNameBlankSpace_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                " ",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmFirstNameBlank_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmFirstNameNotBlank_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }


    // CustomerRequestDTO ctmLastName : @NotBlank
    @Test
    public void validCustomerWithCtmLastNameNull_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                null,
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmLastNameBlankSpace_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                " ",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmLastNameBlank_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmLastNameNotBlank_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    // CustomerRequestDTO ctmEmail : @NotBlank
    @Test
    public void validCustomerWithCtmEmailNull_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                null,
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailBlankSpace_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                " ",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailBlank_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailNotBlank_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    // CustomerRequestDTO ctmEmail : @Email
    @Test
    public void validCustomerWithCtmEmailNotValid_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "invalid-email",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "Email"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailValid_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "Email"
        );
        Assertions.assertFalse(constraintExist);
    }

    // CustomerRequestDTO ctmPhone : @NotBlank
    @Test
    public void validCustomerWithCtmPhoneNull_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                null,
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneBlankSpace_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                " ",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneBlank_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneNotBlank_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    // CustomerRequestDTO ctmPhone : @ValidPhoneNumber
    @Test
    public void validCustomerWithCtmPhoneNotValid_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "invalid-phone",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "ValidPhoneNumber"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneValid_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "ValidPhoneNumber"
        );
        Assertions.assertFalse(constraintExist);
    }

    // CustomerRequestDTO ctmAddress : @NotNull
    @Test
    public void validCustomerWithAddressIdNull_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO("John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                null,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "addId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithAddressIdNotNull_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "addId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }

    // CustomerRequestDTO accountType : @NotNull
    @Test
    public void validCustomerWithAccountTypeNull_MustNotBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO("John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                null,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "accTypeId",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithAccountIdNotNull_MustBeValidated() {

        CustomerRequestDTO customer = new CustomerRequestDTO(
                "John",
                "Doe",
                "John.Doe@gmail.com",
                "+33617755534",
                1L,
                3L,
                List.of(1L, 2L)
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "accTypeId",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }
}
