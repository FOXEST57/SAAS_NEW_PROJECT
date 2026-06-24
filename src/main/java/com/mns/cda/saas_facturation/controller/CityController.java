package com.mns.cda.saas_facturation.controller;

import com.mns.cda.saas_facturation.model.City;
import com.mns.cda.saas_facturation.service.ICityService;
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
    public ResponseEntity<List<City>> getCities() {
        return new ResponseEntity<>(cityService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{cityId}")
    public ResponseEntity<City> getCityById(@PathVariable Long cityId) {
        Optional<City> optionalCity = cityService.findById(cityId);

        if (optionalCity.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(optionalCity.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> createCity(@RequestBody @Valid City city) {
        cityService.create(city);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{cityId}")
    public ResponseEntity<City> modifyCity(@PathVariable Long cityId, @RequestBody @Valid City city) {
        try {
            cityService.modify(cityId, city);
            return new ResponseEntity<>(city, HttpStatus.OK);
        } catch (ICityService.CityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{cityId}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long cityId) {
        try {
            cityService.delete(cityId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ICityService.CityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
