package com.carRental.service;

import com.carRental.domain.Car;
import com.carRental.domain.Rental;
import com.carRental.domain.Status;
import com.carRental.domain.User;
import com.carRental.domain.dto.RentalDto;
import com.carRental.domain.dto.RentalExtensionDto;
import com.carRental.exceptions.CarNotFoundException;
import com.carRental.exceptions.RentalNotFoundException;
import com.carRental.exceptions.UserNotFoundException;
import com.carRental.repository.CarRepository;
import com.carRental.repository.RentalRepository;
import com.carRental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Transactional
@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;

    @Autowired
    public RentalService(RentalRepository rentalRepository, UserRepository userRepository, CarRepository carRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    public Rental getRentalById(Long id) throws RentalNotFoundException {
        return rentalRepository.findById(id).orElseThrow(RentalNotFoundException::new);
    }

    public List<Rental> getRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getRentalsByUserId(Long id) {
        List<Rental> rentalList = getRentals();
        return rentalList.stream()
                .filter(r -> r.getUser().getId().equals(id))
                .collect(Collectors.toList());
    }

    public Rental createRental(RentalDto rentalDto) throws UserNotFoundException, CarNotFoundException {
        User user = userRepository.findById(rentalDto.getUserId()).orElseThrow(UserNotFoundException::new);
        Car car = carRepository.findById(rentalDto.getCarId()).orElseThrow(CarNotFoundException::new);
        car.setStatus(Status.RENTED);

        Rental rental = new Rental(
                rentalDto.getRentedFrom(),
                rentalDto.getRentedTo(),
                user,
                car);

        return rentalRepository.save(rental);
    }

    public Rental modifyRental(RentalDto rentalDto) throws UserNotFoundException, CarNotFoundException, RentalNotFoundException {
        User user = userRepository.findById(rentalDto.getUserId()).orElseThrow(UserNotFoundException::new);
        Car car = carRepository.findById(rentalDto.getCarId()).orElseThrow(CarNotFoundException::new);
        Rental rental = rentalRepository.findById(rentalDto.getId()).orElseThrow(RentalNotFoundException::new);

        rental.setUser(user);
        rental.setCar(car);
        rental.setRentedFrom(rentalDto.getRentedFrom());
        rental.setRentedTo(rentalDto.getRentedTo());
        updateDuration(rental);
        updateCost(rental);

        return rental;
    }

    public Rental extendRental(RentalExtensionDto rentalExtensionDto) throws RentalNotFoundException {
        Rental rental = rentalRepository.findById(rentalExtensionDto.getRentalId()).orElseThrow(RentalNotFoundException::new);

        LocalDate updatedReturnDate = rental.getRentedTo().plusDays(rentalExtensionDto.getExtension());
        rental.setRentedTo(updatedReturnDate);
        updateDuration(rental);
        updateCost(rental);

        return rental;
    }

    public void closeRental(Long id) throws RentalNotFoundException {
        Rental rental = rentalRepository.findById(id).orElseThrow(RentalNotFoundException::new);

        rental.getUser().getRentals().remove(rental);
        rental.getCar().getRentals().remove(rental);
        rental.getCar().setStatus(Status.AVAILABLE);

        rentalRepository.deleteById(id);
    }

    public void updateDuration(Rental rental) {
        if (rental.getRentedTo().isAfter(rental.getRentedFrom())) {
            rental.setDuration(DAYS.between(rental.getRentedFrom(), rental.getRentedTo()));
        } else {
            rental.setDuration(0L);
        }
    }

    public void updateCost(Rental rental) {
        BigDecimal updatedCost = rental.getCar().getCostPerDay().multiply(new BigDecimal(rental.getDuration()));
        rental.setCost(updatedCost);
    }
}
