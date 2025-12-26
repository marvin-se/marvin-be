package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.constants.RequestType;
import com.marvin.campustrade.constants.TokenType;
import com.marvin.campustrade.data.dto.auth.*;
import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.dto.user.BlockResponse;
import com.marvin.campustrade.data.entity.*;
import com.marvin.campustrade.data.mapper.BlockMapper;
import com.marvin.campustrade.data.mapper.UserMapper;
import com.marvin.campustrade.exception.EmailAlreadyExistsException;
import com.marvin.campustrade.repository.*;
import com.marvin.campustrade.service.EmailService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private UsersBlockRepository usersBlockRepository;
    @Mock private UniversityRepository universityRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private EmailService emailService;
    @Mock private BlockMapper blockMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private Users user;
    private University university;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // ---- authenticated user ----
        user = new Users();
        user.setId(1L);
        user.setEmail("test@itu.edu.tr");
        user.setIsActive(true);
        user.setIsVerified(true);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test@itu.edu.tr",
                        null,
                        List.of()
                )
        );

        // ---- university ----
        university = new University();
        university.setId(10L);
        university.setName("ITU");
        university.setDomain("itu.edu.tr");

        // ---- register request ----
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@itu.edu.tr");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setUniversity("ITU");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --------------------------------------------------
    // createUser
    // --------------------------------------------------

    @Test
    void createUser_success() {

        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(universityRepository.findByName("ITU"))
                .thenReturn(Optional.of(university));

        when(passwordEncoder.encode(any()))
                .thenReturn("encoded");

        when(userMapper.toEntity(registerRequest))
                .thenReturn(user);

        when(userMapper.toResponse(any()))
                .thenReturn(new UserResponse(
                        1L, "Test User", "test@itu.edu.tr",
                        null, 10L, "ITU",
                        null, LocalDateTime.now(), true
                ));

        UserResponse response = userService.createUser(registerRequest);

        assertNotNull(response);
        verify(userRepository).save(any());
        verify(tokenRepository).save(any());
        verify(emailService).sendVerificationEmail(eq("test@itu.edu.tr"), any());
    }

    @Test
    void createUser_throwsException_whenEmailExistsAndActive() {

        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(registerRequest)
        );
    }

    // --------------------------------------------------
    // verifyUser
    // --------------------------------------------------

    @Test
    void verifyUser_success() {

        VerifyRequest request = new VerifyRequest();
        request.setEmail("test@itu.edu.tr");
        request.setToken("123456");

        Token token = new Token();
        token.setContent("123456");
        token.setType(TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(tokenRepository.findByUserAndType(user, TokenType.EMAIL_VERIFICATION))
                .thenReturn(Optional.of(token));

        userService.verifyUser(request);

        assertTrue(user.getIsVerified());
        verify(tokenRepository).delete(token);
        verify(userRepository).save(user);
    }

    // --------------------------------------------------
    // generateResetEmail
    // --------------------------------------------------

    @Test
    void generateResetEmail_createsToken() {

        when(userRepository.findByEmail("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(tokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET))
                .thenReturn(Optional.empty());

        userService.generateResetEmail(new ForgotPasswordRequest("test@itu.edu.tr"));

        verify(tokenRepository).save(any());
        verify(emailService).sendResetEmail(eq("test@itu.edu.tr"), any());
    }

    // --------------------------------------------------
    // verifyResetCode
    // --------------------------------------------------

    @Test
    void verifyResetCode_success() {

        VerifyRequest request = new VerifyRequest();
        request.setEmail("test@itu.edu.tr");
        request.setToken("999999");

        Token token = new Token();
        token.setContent("999999");
        token.setType(TokenType.PASSWORD_RESET);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(tokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET))
                .thenReturn(Optional.of(token));

        userService.verifyResetCode(request);

        assertTrue(token.getIsVerified());
        verify(tokenRepository).save(token);
    }

    // --------------------------------------------------
    // changePassword
    // --------------------------------------------------

    @Test
    void changePassword_forgotPassword_success() {

        ChangePassword request = new ChangePassword();
        request.setType(RequestType.FORGOT_PASSWORD);
        request.setEmail("test@itu.edu.tr");
        request.setToken("111111");
        request.setNewPassword("newPass");
        request.setConfirmNewPassword("newPass");

        Token token = new Token();
        token.setContent("111111");
        token.setType(TokenType.PASSWORD_RESET);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(tokenRepository.findByUserAndType(user, TokenType.PASSWORD_RESET))
                .thenReturn(Optional.of(token));

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encoded");

        userService.changePassword(request);

        verify(userRepository).save(user);
        verify(tokenRepository).delete(token);
    }

    @Test
    void changePassword_changePassword_success() {

        ChangePassword request = new ChangePassword();
        request.setType(RequestType.CHANGE_PASSWORD);
        request.setOldPassword("old");
        request.setNewPassword("new");
        request.setConfirmNewPassword("new");

        when(userRepository.findByEmail("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("new"))
                .thenReturn("encoded");

        userService.changePassword(request);

        verify(userRepository).save(user);
    }

    @Test
    void unblockUser_throwsException_whenNotBlocked() {

        Users target = new Users();
        target.setId(2L);
        target.setIsActive(true);
        target.setIsVerified(true);

        when(userRepository.findByEmail("test@itu.edu.tr"))
                .thenReturn(Optional.of(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(target));

        when(usersBlockRepository.findByBlockerAndBlocked(user, target))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.unblockUser("2"));
    }
}
