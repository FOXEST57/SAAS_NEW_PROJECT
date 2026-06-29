package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.DTO.CityDTO;
import com.mns.cda.saas_facturation.DTO.CityRequestDTO;
import com.mns.cda.saas_facturation.Iservice.IPostalCodeService;
import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.Iservice.ICityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/city")
@RestController
public class CityController {

    private final ICityService cityService;

    @GetMapping("/list")
    public ResponseEntity<List<CityDTO>> getCities() {
        return new ResponseEntity<>(cityService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<CityDTO> getCityById(@PathVariable Long cityId) {
        Optional<CityDTO> optionalCity = cityService.findById(cityId);

        if (optionalCity.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCity.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> createCity(@RequestBody @Valid CityRequestDTO dto) throws IPostalCodeService.PostalCodeNotFoundException {
        cityService.create(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{cityId}")
    public ResponseEntity<CityDTO> modifyCity(@PathVariable Long cityId, @RequestBody @Valid CityRequestDTO dto) throws ICityService.CityNotFoundException, IPostalCodeService.PostalCodeNotFoundException {
        CityDTO cityModified = cityService.modify(cityId, dto);

        return new ResponseEntity<>(cityModified, HttpStatus.OK);
    }

    @DeleteMapping("{cityId}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long cityId) throws ICityService.CityNotFoundException {
        cityService.delete(cityId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
