package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.PostalCodeDTO;
import com.mns.cda.saas_facturation.model.PostalCode;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/postalCode")
@RestController
public class PostalCodeController {

    private final IPostalCodeService postalCodeService;

    @GetMapping("/list")
    public ResponseEntity<List<PostalCodeDTO>> getPostalCodes() {
        return new ResponseEntity<>(postalCodeService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{pcodeId}")
    public ResponseEntity<PostalCodeDTO> getPostalCodeById(@PathVariable Long pcodeId) {
        Optional<PostalCodeDTO> optionalPostalCode = postalCodeService.findById(pcodeId);

        if (optionalPostalCode.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalPostalCode.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> createPostalCode(@RequestBody @Valid PostalCode postalCode) {
        postalCodeService.create(postalCode);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{pcodeId}")
    public ResponseEntity<PostalCodeDTO> modifyPostalCode(@PathVariable Long pcodeId, @RequestBody @Valid PostalCode postalCode) {
        Optional<PostalCodeDTO> optionalPostalCode = postalCodeService.findById(pcodeId);

        if (optionalPostalCode.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PostalCodeDTO postalCodeModified = postalCodeService.modify(pcodeId, postalCode);

        return new ResponseEntity<>(postalCodeModified, HttpStatus.OK);
    }

    @DeleteMapping("{pcodeId}")
    public ResponseEntity<Void> deletePostalCode(@PathVariable Long pcodeId) {
        Optional<PostalCodeDTO> optionalPostalCode = postalCodeService.findById(pcodeId);

        if (optionalPostalCode.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        postalCodeService.delete(pcodeId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
