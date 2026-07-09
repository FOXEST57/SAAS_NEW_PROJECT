package com.mns.cda.saas_facturation.unitaire.dto.update;

import com.mns.cda.saas_facturation.DTO.updateDTO.UpdateMakerReferenceDTO;
import com.mns.cda.saas_facturation.TestUtilitaire;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UpdateMakerReferenceDTOUnitTest {

    public static Validator validator;

    @BeforeAll
    public static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // UpdateMakerReferenceDTO mkrRefReference : @NotBlank
    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceNull_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                null
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceBlankSpace_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "  "
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceBlank_MustNotBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                ""
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertTrue(constraintExist);
    }

    @Test
    public void validUpdateMakerReferenceDTOWithMkrRefReferenceNotBlank_MustBeValidated() {

        UpdateMakerReferenceDTO updateMakerReferenceDTO = new UpdateMakerReferenceDTO(
                "REF123"
        );

        boolean constraintExist = TestUtilitaire.constraintViolationExist(
                validator.validate(updateMakerReferenceDTO),
                "reference",
                "NotBlank"
        );
        Assertions.assertFalse(constraintExist);
    }
}
