package com.mns.cda.saas_facturation.unitaire.dto;

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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                null,
                0,
                null,
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
                null,
                "Test",
                null,
                null,
                0,
                null,
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
                null,
                "",
                null,
                null,
                0,
                null,
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
                null,
                " ",
                null,
                null,
                0,
                null,
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
                null,
                " test ",
                null,
                null,
                0,
                null,
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
                null,
                "\t",
                null,
                null,
                0,
                null,
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
                null,
                "\n",
                null,
                null,
                0,
                null,
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
                null,
                " \n \t ",
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                "Test",
                null,
                0,
                null,
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
                null,
                null,
                "",
                null,
                0,
                null,
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
                null,
                null,
                " ",
                null,
                0,
                null,
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
                null,
                null,
                " test ",
                null,
                0,
                null,
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
                null,
                null,
                "\t",
                null,
                0,
                null,
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
                null,
                null,
                "\n",
                null,
                0,
                null,
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
                null,
                null,
                " \n \t ",
                null,
                0,
                null,
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
                null,
                null,
                null,
                BigDecimal.valueOf(1),
                0,
                null,
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
                null,
                null,
                null,
                null,
                0,
                null,
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
                null,
                null,
                null,
                BigDecimal.valueOf(-1),
                0,
                null,
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
                null,
                null,
                null,
                BigDecimal.valueOf(0),
                0,
                null,
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
                null,
                null,
                null,
                BigDecimal.valueOf(0.1),
                0,
                null,
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
                null,
                null,
                null,
                null,
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
                null,
                null,
                null,
                null,
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
                null,
                null,
                null,
                null,
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
