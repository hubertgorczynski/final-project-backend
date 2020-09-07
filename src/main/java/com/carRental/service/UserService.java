package com.carRental.service;

import com.carRental.domain.User;
import com.carRental.domain.dto.UserDto;
import com.carRental.exceptions.UserNotFoundException;
import com.carRental.mapper.UserMapper;
import com.carRental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User saveUser(final UserDto userDto) {
        return userRepository.save(userMapper.mapToUser(userDto));
    }

    public UserDto getUserById(Long id) throws UserNotFoundException {
        return userMapper.mapToUserDto(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        return userMapper.mapToUserDto(userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new));
    }

    public UserDto getUserByPhoneNumber(int phoneNumber) throws UserNotFoundException {
        return userMapper.mapToUserDto(userRepository.findByPhoneNumber(phoneNumber).orElseThrow(UserNotFoundException::new));
    }

    public List<UserDto> getAllUsers() {
        return userMapper.mapToUserDtoList(userRepository.findAll());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}