package com.mns.cda.saas_facturation.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.mns.cda.saas_facturation.Model.SupplierModel;
import com.mns.cda.saas_facturation.Service.ISupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.function.SupplierUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class SupplierController {

    protected final ISupplierService supplierService;

    @GetMapping("/supplier/list")
    public List<SupplierModel> getAll() {
        return supplierService.findAll();
    }

    @GetMapping("/supplier/{id}")
    public ResponseEntity<SupplierModel> get(@PathVariable long id) {
        return ResponseEntity.ok(supplierService.findById(id));
    }

    @PostMapping("/supplier")
    public ResponseEntity<SupplierModel> create(
            @RequestBody
            SupplierModel supplierToInsert) {

        supplierService.create(supplierToInsert);

        return new ResponseEntity<>(supplierToInsert, HttpStatus.CREATED);

    }

    @DeleteMapping("/supplier/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/supplier/{id}")
    public ResponseEntity<Void> update(@PathVariable long id,
                                       @RequestBody SupplierModel supplierToUpdate) {
        supplierService.update(id, supplierToUpdate);
        return ResponseEntity.noContent().build();
    }



}
