package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.DTO.requestDTO.CategoryRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CategoryRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // CategoryRequestDTO catName : @NotBlank
    @Test
    public void validCategoryWithCatNameNull_MustNotBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                null,
                "electronics",
                1L
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatNameBlankSpace_MustNotBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                " ",
                "electronics",
                1L
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatNameBlank_MustNotBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                "",
                "electronics",
                1L
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatNameNotBlank_MustBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                "Electronics",
                "electronics",
                1L
        );


        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    // CategoryRequestDTO catSlug : @NotBlank
    @Test
    public void validCategoryWithCatSlugNull_MustNotBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                "Electronics",
                null,
                1L
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatSlugBlankSpace_MustNotBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                "Electronics",
                " ",
                1L
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatSlugBlank_MustNotBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                "Electronics",
                "",
                1L
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatSlugNotBlank_MustBeValidated() {

        CategoryRequestDTO category = new CategoryRequestDTO(
                "Electronics",
                "electronics",
                1L
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }
}
