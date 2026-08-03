package com.mns.cda.saas_facturation.unitaire.dto.update;

import com.mns.cda.saas_facturation.product.DTO.updateDTO.ArticleUpdateDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

public class ArticleUpdateDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // ArticleUpdateDTO artReference: @NotBlank
    @Test
    public void validArticleWithArtReferenceNull_MustBeNotValidated() {

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                null,
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "Test",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                " ",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                " test ",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "\t",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "\n",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                " \n \t ",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artReference",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleUpdateDTO artName: @NotBlank
    @Test
    public void validArticleWithArtNameNull_MustBeNotValidated() {

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                null,
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Test",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                " ",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                " test ",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "\t",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "\n",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                " \n \t ",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artName",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleUpdateDTO artDescription: @NotBlank
    @Test
    public void validArticleWithArtDescriptionNull_MustBeNotValidated() {

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                null,
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Test",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                " ",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                " test ",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "\t",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "\n",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                " \n \t ",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artDescription",
                "NotBlank"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleUpdateDTO ArtPriceExcludeTaxes: @NotNull
    @Test
    public void validArticleWithArtPriceExcludeTaxesNotNull_MustBeValidated() {

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                null,
                0,
                1L,
                List.of()
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "NotNull"
        );

        Assertions.assertTrue(constraintViolation);
    }

    // ArticleUpdateDTO ArtPriceExcludeTaxes : @DecimalMin
    @Test
    public void validArticleWithArtPriceExcludeTaxesNegative_MustNotBeValidated() {

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.valueOf(-1),
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ZERO,
                0,
                1L,
                List.of()
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "artPriceExcludeTaxes",
                "DecimalMin"
        );

        Assertions.assertTrue(constraintViolation);
    }

    @Test
    public void validArticleWithArtPriceExcludeTaxesPositive_MustBeValidated() {

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.valueOf(0.1),
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                null,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                1L,
                List.of()
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

        ArticleUpdateDTO article = new ArticleUpdateDTO(
                "REF001",
                "Article",
                "Description",
                BigDecimal.ONE,
                0,
                -1L,
                List.of()
        );

        boolean constraintViolation = TestUtilitaire.constraintViolationExist(
                validator.validate(article),
                "tvaId",
                "Min"
        );

        Assertions.assertTrue(constraintViolation);
    }
}
