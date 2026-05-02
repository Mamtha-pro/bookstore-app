package com.bookstore.service.impl;

import com.bookstore.dto.request.ChangePasswordRequest;
import com.bookstore.dto.request.UpdateProfileRequest;
import com.bookstore.dto.response.UserResponse;
import com.bookstore.entity.User;
import com.bookstore.exception.BadRequestException;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.mapper.UserMapper;
import com.bookstore.repository.UserRepository;
import com.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public UserResponse getMyProfile(String email) {
        return UserMapper.toResponse(findByEmail(email));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email,
                                      UpdateProfileRequest request) {
        User user = findByEmail(email);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        log.info("Profile updated for: " + email);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(String email,
                               ChangePasswordRequest request) {
        User user = findByEmail(email);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for: " + email);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        return UserMapper.toResponse(
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found: " + id)));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + id));
        userRepository.deleteById(id);
        log.info("User deleted: " + id);
    }
}