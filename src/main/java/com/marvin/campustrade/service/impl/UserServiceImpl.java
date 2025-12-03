package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.data.dto.auth.RegisterRequest;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.entity.University;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.UserMapper;
import com.marvin.campustrade.exception.EmailAlreadyExistsException;
import com.marvin.campustrade.exception.InvalidStudentEmailDomainException;
import com.marvin.campustrade.exception.UniversityNotFoundException;
import com.marvin.campustrade.repository.UniversityRepository;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(RegisterRequest request){
        // Check if the email is already in use
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyExistsException("Email is already registered");
        }
        // TO DO: Do we have all possible universities in the database?
        // Load university
        University university = universityRepository.findByName(request.getUniversity())
                .orElseThrow(() -> new UniversityNotFoundException("University not found"));

        // Validate student email domain
        String email = request.getEmail();
        String requiredDomain = university.getDomain();   // e.g. "itu.edu.tr"

        if (!email.toLowerCase().endsWith("@" + requiredDomain.toLowerCase())) {
            throw new InvalidStudentEmailDomainException(
                    "Email must be a student email ending with @" + requiredDomain
            );
        }

        Users user = userMapper.toEntity(request);
        user.setUniversity(university);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        Users saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }

}
