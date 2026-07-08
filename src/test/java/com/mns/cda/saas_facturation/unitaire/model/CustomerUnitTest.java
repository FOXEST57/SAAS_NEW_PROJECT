package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.Address;
import com.mns.cda.saas_facturation.model.Customer;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CustomerUnitTest {
    
    public static Validator validator;
    
    @BeforeAll
    public static void init(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Customer ctmFirstName : @NotBlank
    @Test
    public void validCustomerWithCtmFirstNameNull_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmFirstName(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmFirstNameBlankSpace_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmFirstName("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmFirstNameBlank_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmFirstName("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmFirstNameNotBlank_MustBeValidated() {

        Customer customer = new Customer();
        customer.setCtmFirstName("John");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmFirstName",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }


    // Customer ctmLastName : @NotBlank
    @Test
    public void validCustomerWithCtmLastNameNull_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmLastName(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmLastNameBlankSpace_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmLastName("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmLastNameBlank_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmLastName("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmLastNameNotBlank_MustBeValidated() {

        Customer customer = new Customer();
        customer.setCtmLastName("Doe");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmLastName",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    // Customer ctmEmail : @NotBlank
    @Test
    public void validCustomerWithCtmEmailNull_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmEmail(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailBlankSpace_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmEmail("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailBlank_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmEmail("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailNotBlank_MustBeValidated() {

        Customer customer = new Customer();
        customer.setCtmEmail("JohnDoe@gmail.com");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    // Customer ctmEmail : @Email
    @Test
    public void validCustomerWithCtmEmailNotValid_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmEmail("invalid-email");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "Email"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmEmailValid_MustBeValidated() {

        Customer customer = new Customer();
        customer.setCtmEmail("JohnDoe@gmail.com");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmEmail",
                "Email"
        );
        Assertions.assertFalse(constraintExist);
    }

    // Customer ctmPhone : @NotBlank
    @Test
    public void validCustomerWithCtmPhoneNull_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmPhone(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneBlankSpace_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmPhone("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneBlank_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmEmail("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneNotBlank_MustBeValidated() {

        Customer customer = new Customer();
        customer.setCtmPhone("+33234567890");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }

    // Customer ctmPhone : @ValidPhoneNumber
    @Test
    public void validCustomerWithCtmPhoneNotValid_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setCtmPhone("invalid-phone");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "ValidPhoneNumber"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmPhoneValid_MustBeValidated() {

        Customer customer = new Customer();
        customer.setCtmPhone("+33234567890");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "ctmPhone",
                "ValidPhoneNumber"
        );
        Assertions.assertFalse(constraintExist);
    }

    // Customer ctmAddress : @NotNull
    @Test
    public void validCustomerWithCtmAddressNull_MustNotBeValidated() {

        Customer customer = new Customer();
        customer.setAddress(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "address",
                "NotNull"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCustomerWithCtmAddressNotNull_MustBeValidated() {

        Customer customer = new Customer();
        customer.setAddress(new Address());

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(customer),
                "address",
                "NotNull"
        );
        Assertions.assertFalse(constraintExist);
    }
}
