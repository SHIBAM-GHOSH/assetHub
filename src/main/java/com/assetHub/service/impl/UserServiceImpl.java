package com.assetHub.service.impl;

import com.assetHub.dto.user.UserRegistrationRequest;
import com.assetHub.dto.user.UserResponse;
import com.assetHub.entity.Role;
import com.assetHub.entity.User;
import com.assetHub.exception.ResourceAlreadyExistsException;
import com.assetHub.repository.UserRepository;
import com.assetHub.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //dependency injection
    //put the belwo modules during object creation of UserServiceImp in IOC contiainer
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRegistrationRequest request) {

        // Check whether the email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "User already exists with email: " + request.getEmail()
            );
        }

        // Convert Request DTO to User Entity
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));        
        user.setRole(Role.USER);

        // Save User Entity to database
        User savedUser = userRepository.save(user);

        // Convert saved User Entity to Response DTO
        return new UserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
    }
}