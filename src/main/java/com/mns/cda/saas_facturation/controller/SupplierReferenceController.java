package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.SupplierReferenceDTO;
import com.mns.cda.saas_facturation.DTO.requestDTO.SupplierReferenceRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IArticleService;
import com.mns.cda.saas_facturation.Iservice.ISupplierReferenceService;
import com.mns.cda.saas_facturation.Iservice.ISupplierService;
import com.mns.cda.saas_facturation.model.SupplierReference;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supplier-reference")
@Tag(name = "Supplier Reference", description = "API de gestion des différents référence supplier.")
@CrossOrigin
public class SupplierReferenceController {

    private final ISupplierReferenceService supplierReferenceService;

    @GetMapping("/list")
    public List<SupplierReferenceDTO> findAll() {
        return supplierReferenceService.findAll();
    }

    @GetMapping("/{articleId}/{supplierId}")
    public ResponseEntity<SupplierReferenceDTO> findById(
            @PathVariable Long articleId,
            @PathVariable Long supplierId) {

        SupplierReference.SupplierReferenceId supplierReferenceId = new SupplierReference.SupplierReferenceId(articleId, supplierId);
        Optional<SupplierReferenceDTO> supplierReference = supplierReferenceService.findById(supplierReferenceId);

        if (supplierReference.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(supplierReference.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SupplierReferenceDTO> create(
            @Valid @RequestBody SupplierReferenceRequestDTO supplierReference)
            throws IArticleService.ArticleNotFoundException,
            ISupplierService.SupplierNotFoundException {

        SupplierReferenceDTO response = supplierReferenceService.create(supplierReference);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{articleId}/{supplierId}")
    public ResponseEntity<SupplierReferenceDTO> delete(
            @PathVariable Long articleId,
            @PathVariable Long supplierId) {

        SupplierReference.SupplierReferenceId supplierReferenceId = new SupplierReference.SupplierReferenceId(articleId, supplierId);
        Optional<SupplierReferenceDTO> supplierReference = supplierReferenceService.findById(supplierReferenceId);

        if (supplierReference.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        supplierReferenceService.deleteById(supplierReferenceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{articleId}/{supplierId}")
    public ResponseEntity<SupplierReferenceDTO> update(
            @PathVariable Long articleId,
            @PathVariable Long supplierId,
            @Valid @RequestBody SupplierReferenceRequestDTO dto
    ) throws ISupplierService.SupplierNotFoundException,
            IArticleService.ArticleNotFoundException,
            ISupplierReferenceService.SupplierReferenceNotFoundException {

        SupplierReference.SupplierReferenceId artSplId = new SupplierReference.SupplierReferenceId(articleId, supplierId);

        SupplierReferenceDTO updated = supplierReferenceService.update(artSplId, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}

