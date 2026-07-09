package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.model.Category;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CategoryUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Category catName : @NotBlank
    @Test
    public void validCategoryWithCatNameNull_MustNotBeValidated() {

        Category category = new Category();
        category.setCatName(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatNameBlankSpace_MustNotBeValidated() {

        Category category = new Category();
        category.setCatName("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatNameBlank_MustNotBeValidated() {

        Category category = new Category();
        category.setCatName("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatNameNotBlank_MustBeValidated() {

        Category category = new Category();
        category.setCatName("Electronics");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    // Category catSlug : @NotBlank
    @Test
    public void validCategoryWithCatSlugNull_MustNotBeValidated() {

        Category category = new Category();
        category.setCatSlug(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatSlugBlankSpace_MustNotBeValidated() {

        Category category = new Category();
        category.setCatSlug("  ");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatSlugBlank_MustNotBeValidated() {

        Category category = new Category();
        category.setCatSlug("");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validCategoryWithCatSlugNotBlank_MustBeValidated() {

        Category category = new Category();
        category.setCatSlug("electronics");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(category),
                "catSlug",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }
}
