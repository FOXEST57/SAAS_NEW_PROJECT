package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.model.PostalCode;
import com.mns.cda.saas_facturation.service.IPostalCodeService;
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
    public ResponseEntity<List<PostalCode>> getPostalCodes() {
        return new ResponseEntity<>(postalCodeService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{pcodeId}")
    public ResponseEntity<PostalCode> getPostalCodeById(@PathVariable Long pcodeId) {
        Optional<PostalCode> optionalPostalCode = postalCodeService.findById(pcodeId);

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
    public ResponseEntity<PostalCode> modifyPostalCode(@PathVariable Long pcodeId, @RequestBody @Valid PostalCode postalCode) {
        try {
            postalCodeService.modify(pcodeId, postalCode);
            return new ResponseEntity<>(postalCode, HttpStatus.OK);
        } catch (IPostalCodeService.PostalCodeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{pcodeId}")
    public ResponseEntity<Void> deletePostalCode(@PathVariable Long pcodeId) {
        try {
            postalCodeService.delete(pcodeId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IPostalCodeService.PostalCodeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
