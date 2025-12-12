package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.constants.RequestType;
import com.marvin.campustrade.constants.TokenType;
import com.marvin.campustrade.data.dto.auth.*;
import com.marvin.campustrade.data.entity.University;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.entity.Token;
import com.marvin.campustrade.data.mapper.UserMapper;
import com.marvin.campustrade.exception.EmailAlreadyExistsException;
import com.marvin.campustrade.exception.InvalidStudentEmailDomainException;
import com.marvin.campustrade.exception.UniversityNotFoundException;
import com.marvin.campustrade.repository.UniversityRepository;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.repository.TokenRepository;
import com.marvin.campustrade.service.EmailService;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;

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
        user.setIsVerified(false);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        // create token
        String code = String.format("%06d", new Random().nextInt(999999));
        Token token = new Token();
        token.setContent(code);
        token.setType(TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        token.setUser(user);
        tokenRepository.save(token);

        // send email
        emailService.sendVerificationEmail(user.getEmail(), token);

        return userMapper.toResponse(user);
    }

    @Override
    public Users getCurrentUser(){
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public void verifyUser(VerifyRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Token userToken = tokenRepository.findByUserAndType(user, TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (userToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token is expired");
        }

        if(!userToken.getContent().equals(request.getToken())){
            throw new RuntimeException("Token does not match");
        }

        if(!userToken.getType().equals(TokenType.EMAIL_VERIFICATION)){
            throw new RuntimeException("Token type is not verification token.");
        }
        user.setIsVerified(true);
        tokenRepository.delete(userToken);
        userRepository.save(user);
    }

    @Override
    public void generateResetEmail(ForgotPasswordRequest request) {
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String code = String.format("%06d", new Random().nextInt(999999));
        Token token = new Token();
        token.setContent(code);
        token.setType(TokenType.PASSWORD_RESET);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        token.setUser(user);
        tokenRepository.save(token);
        emailService.sendResetEmail(user.getEmail(), token);
    }
    @Override
    public void verifyResetCode(VerifyRequest request){
        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Token userToken = tokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (userToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token is expired");
        }

        if(!userToken.getContent().equals(request.getToken())){
            throw new RuntimeException("Token does not match");
        }

        if(!userToken.getType().equals(TokenType.PASSWORD_RESET)){
            throw new RuntimeException("Token type is not reset token.");
        }
        userToken.setIsVerified(true);
        tokenRepository.save(userToken);

    }

    @Override
    public void changePassword(ChangePassword request) {

        if (request.getType() == RequestType.FORGOT_PASSWORD) {

            Users user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Token token = tokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET)
                    .orElseThrow(() -> new RuntimeException("Reset token not found"));

            // Validate token again for security
            if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Token is expired");
            }

            if (!token.getContent().equals(request.getToken())) {
                throw new RuntimeException("Invalid reset token");
            }

            // Check passwords
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw new RuntimeException("Passwords do not match");
            }

            // Update password
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Delete token after successful password reset
            tokenRepository.delete(token);
        }
    }

}
