package com.carRental.service;

import com.carRental.domain.Car;
import com.carRental.domain.Rental;
import com.carRental.domain.User;
import com.carRental.domain.dto.RentalDto;
import com.carRental.domain.dto.RentalExtensionDto;
import com.carRental.exceptions.CarNotFoundException;
import com.carRental.exceptions.RentalNotFoundException;
import com.carRental.exceptions.UserNotFoundException;
import com.carRental.repository.CarRepository;
import com.carRental.repository.RentalRepository;
import com.carRental.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentalServiceTestSuite {

    @InjectMocks
    private RentalService rentalService;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CarRepository carRepository;

    User user = new User(
            1L,
            "Jack",
            "Smith",
            "email",
            "password",
            123456);

    Car car = new Car(
            1L,
            "sampleVin",
            "Audi",
            "A3",
            2015,
            "Diesel",
            3.0,
            "Saloon",
            110000,
            new BigDecimal(18));

    Rental rental = new Rental(
            LocalDate.of(2020, 10, 10),
            LocalDate.of(2020, 10, 15),
            user,
            car);

    RentalDto rentalDto = new RentalDto(
            1L,
            LocalDate.of(2020, 10, 10),
            LocalDate.of(2020, 10, 15),
            1L,
            1L);

    @Test
    public void modifyRentalTest() throws UserNotFoundException, RentalNotFoundException, CarNotFoundException {
        //Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));

        //When
        Rental modifiedRental = rentalService.modifyRental(rentalDto);

        //Then
        assertEquals(rentalDto.getUserId(), modifiedRental.getUser().getId());
        assertEquals(rentalDto.getCarId(), modifiedRental.getCar().getId());
        assertEquals(rentalDto.getRentedFrom(), modifiedRental.getRentedFrom());
        assertEquals(rentalDto.getRentedTo(), modifiedRental.getRentedTo());
    }

    @Test
    public void extendRentalTest() throws RentalNotFoundException {
        //Given
        RentalExtensionDto rentalExtensionDto = new RentalExtensionDto(1L, 5L);
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));

        //When
        Rental extendedRental = rentalService.extendRental(rentalExtensionDto);

        //Then
        assertEquals(extendedRental.getRentedFrom(), LocalDate.of(2020, 10, 10));
        assertEquals(extendedRental.getRentedTo(), LocalDate.of(2020, 10, 20));
    }

    @Test
    public void closeRentalTest() throws RentalNotFoundException {
        //Given
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));

        //When
        rentalService.closeRental(1L);

        //Then
        verify(rentalRepository, times(1)).deleteById(1L);
    }

    @Test
    public void updateDurationTest() {
        //Given
        rental.setRentedTo(LocalDate.of(2020, 10, 28));

        //When
        rentalService.updateDuration(rental);

        //Then
        assertEquals(18L, (long) rental.getDuration());
    }

    @Test
    public void updateCOstTest() {
        //Given
        rental.setDuration(10L);

        //WHen
        rentalService.updateCost(rental);

        //Then
        assertEquals(new BigDecimal(180), rental.getCost());
    }
}


