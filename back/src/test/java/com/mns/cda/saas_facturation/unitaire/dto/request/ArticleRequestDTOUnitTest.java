package com.mns.cda.saas_facturation.unitaire.dto.request;

import com.mns.cda.saas_facturation.DTO.requestDTO.ArticleRequestDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ArticleRequestDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ArticleRequestDTO artReference: @NotBlank
    @Test
    public void validArticleWithArtReferenceNull_MustBeNotValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                null,
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);


    }

    @Test
    public void validArticleWithArtReferenceNotNull_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "Test",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validArticleWithArtReferenceBlank_MustBeNotValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceSpace_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                " ",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceNotBlankAndSpace_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                " test ",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceTabulation_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "\t",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceLineBreak_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "\n",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtReferenceLineBreakSpaceTabulation_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                " \n \t ",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleRequestDTO artName: @NotBlank
    @Test
    public void validArticleWithArtNameNull_MustBeNotValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                null,
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);


    }

    @Test
    public void validArticleWithArtNameNotNull_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Test",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validArticleWithArtNameBlank_MustBeNotValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameSpace_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                " ",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameNotBlankAndSpace_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                " test ",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameTabulation_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "\t",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameLineBreak_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "\n",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtNameLineBreakSpaceTabulation_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                " \n \t ",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleRequestDTO artDescription: @NotBlank
    @Test
    public void validArticleWithArtDescriptionNull_MustBeNotValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                null,
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintExist);


    }

    @Test
    public void validArticleWithArtDescriptionNotNull_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Test",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertFalse(constraintExist);
    }

    @Test
    public void validArticleWithArtDescriptionBlank_MustBeNotValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionSpace_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                " ",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionNotBlankAndSpace_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                " test ",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionTabulation_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "\t",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionLineBreak_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "\n",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtDescriptionLineBreakSpaceTabulation_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                " \n \t ",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleRequestDTO ArtPriceExcludeTaxes: @NotNull
    @Test
    public void validArticleWithArtPriceExcludeTaxesNotNull_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "NotNull"
        );

        Assertions.assertFalse(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesNull_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                null,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "NotNull"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleRequestDTO ArtPriceExcludeTaxes : @DecimalMin
    @Test
    public void validArticleWithArtPriceExcludeTaxesNegative_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.valueOf(-1),
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "DecimalMin"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesZero_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ZERO,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "DecimalMin"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesPositive_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.valueOf(0.1),
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "DecimalMin"
        );

        Assertions.assertFalse(constraintViolation);
    }

    // ArtRequestDTO TvaId: @NotNull
    @Test
    public void validArticleWithTvaNull_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                null,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "tvaId",
                "NotNull"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithTvaNotNull_MustBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "tvaId",
                "NotNull"
        );

        Assertions.assertFalse(constraintViolation);
    }

    // ArtRequestDTO TvaId: @Min
    @Test
    public void validArticleWithTvaIdNegative_MustNotBeValidated() {

        ArticleRequestDTO article = new ArticleRequestDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                -1L,
                null,
                null
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "tvaId",
                "Min"
        );

        Assertions.assertTrue(constraintViolation);
    }

}
