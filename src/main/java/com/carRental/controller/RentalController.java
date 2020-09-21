package com.carRental.controller;

import com.carRental.domain.dto.RentalDto;
import com.carRental.domain.dto.RentalExtensionDto;
import com.carRental.exceptions.CarNotFoundException;
import com.carRental.exceptions.RentalNotFoundException;
import com.carRental.exceptions.UserNotFoundException;
import com.carRental.facade.RentalFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/rentals")
public class RentalController {

    private final RentalFacade rentalFacade;

    @Autowired
    public RentalController(RentalFacade rentalFacade) {
        this.rentalFacade = rentalFacade;
    }

    @GetMapping("/{id}")
    public RentalDto getRentalById(@PathVariable Long id) throws RentalNotFoundException {
        return rentalFacade.getRentalById(id);
    }

    @GetMapping
    public List<RentalDto> getAllRentals() {
        return rentalFacade.getRentals();
    }

    @PostMapping
    public RentalDto createRental(@RequestBody RentalDto rentalDto) throws CarNotFoundException, UserNotFoundException {
        return rentalFacade.createRental(rentalDto);
    }

    @PutMapping
    public RentalDto modifyRental(@RequestBody RentalDto rentalDto) throws CarNotFoundException, UserNotFoundException {
        return rentalFacade.modifyRental(rentalDto);
    }

    @PutMapping("/extend_rental")
    public RentalDto extendRental(@RequestBody RentalExtensionDto rentalExtensionDto) throws RentalNotFoundException {
        return rentalFacade.extendRental(rentalExtensionDto);
    }

    @DeleteMapping("/{id}")
    public void closeRental(@PathVariable Long id) throws RentalNotFoundException {
        rentalFacade.closeRental(id);
    }
}
