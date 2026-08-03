package com.mns.cda.saas_facturation.unitaire.model;

import com.mns.cda.saas_facturation.TestUtilitaire;
import com.mns.cda.saas_facturation.product.model.Article;
import com.mns.cda.saas_facturation.product.model.Tva;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ArticleUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validArticleWithArtReferenceNull_MustBeNotValidated() {

        Article article = new Article();
        article.setArtReference(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);


    }

    @Test
    public void validArticleWithArtReferenceNotNull_MustBeValidated() {

        Article article = new Article();
        article.setArtReference("Test");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validArticleWithArtReferenceBlank_MustBeNotValidated() {

        Article article = new Article();
        article.setArtReference("");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceSpace_MustNotBeValidated() {

        Article article = new Article();
        article.setArtReference(" ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceNotBlankAndSpace_MustBeValidated() {

        Article article = new Article();
        article.setArtReference(" test ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceTabulation_MustNotBeValidated() {

        Article article = new Article();
        article.setArtReference("\t");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceLineBreak_MustNotBeValidated() {

        Article article = new Article();
        article.setArtReference("\n");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceLineBreakSpaceTabulation_MustNotBeValidated() {

        Article article = new Article();
        article.setArtReference(" \n \t ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameNull_MustBeNotValidated() {

        Article article = new Article();
        article.setArtName(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);


    }

    @Test
    public void validArticleWithArtNameNotNull_MustBeValidated() {

        Article article = new Article();
        article.setArtName("Test");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validArticleWithArtNameBlank_MustBeNotValidated() {

        Article article = new Article();
        article.setArtName("");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameSpace_MustNotBeValidated() {

        Article article = new Article();
        article.setArtName(" ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameNotBlankAndSpace_MustBeValidated() {

        Article article = new Article();
        article.setArtName(" test ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameTabulation_MustNotBeValidated() {

        Article article = new Article();
        article.setArtName("\t");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameLineBreak_MustNotBeValidated() {

        Article article = new Article();
        article.setArtName("\n");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameLineBreakSpaceTabulation_MustNotBeValidated() {

        Article article = new Article();
        article.setArtName(" \n \t ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionNull_MustBeNotValidated() {

        Article article = new Article();
        article.setArtDescription(null);

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);


    }

    @Test
    public void validArticleWithArtDescriptionNotNull_MustBeValidated() {

        Article article = new Article();
        article.setArtDescription("Test");

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validArticleWithArtDescriptionBlank_MustBeNotValidated() {

        Article article = new Article();
        article.setArtDescription("");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionSpace_MustNotBeValidated() {

        Article article = new Article();
        article.setArtDescription(" ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionNotBlankAndSpace_MustBeValidated() {

        Article article = new Article();
        article.setArtDescription(" test ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionTabulation_MustNotBeValidated() {

        Article article = new Article();
        article.setArtDescription("\t");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionLineBreak_MustNotBeValidated() {

        Article article = new Article();
        article.setArtDescription("\n");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionLineBreakSpaceTabulation_MustNotBeValidated() {

        Article article = new Article();
        article.setArtDescription(" \n \t ");

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesNotNull_MustBeValidated() {

        Article article = new Article();
        article.setArtPriceExcludeTaxes(BigDecimal.valueOf(1));

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "NotNull"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesNull_MustNotBeValidated() {

        Article article = new Article();
        article.setArtPriceExcludeTaxes(null);

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "NotNull"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesNegative_MustNotBeValidated() {

        Article article = new Article();
        article.setArtPriceExcludeTaxes(BigDecimal.valueOf(-1));

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "DecimalMin"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesZero_MustNotBeValidated() {

        Article article = new Article();
        article.setArtPriceExcludeTaxes(BigDecimal.valueOf(0));

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "DecimalMin"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesPositive_MustNotBeValidated() {

        Article article = new Article();
        article.setArtPriceExcludeTaxes(BigDecimal.valueOf(0.1));

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "DecimalMin"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithTvaNull_MustNotBeValidated() {

        Article article = new Article();
        article.setTva(null);

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "tva",
                "NotNull"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithTvaNotNull_MustBeValidated() {

        Article article = new Article();
        article.setTva(new Tva());

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "tva",
                "NotNull"
        );

        Assertions.assertFalse(constraintViolation);
    }
}
