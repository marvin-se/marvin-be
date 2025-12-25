package com.marvin.campustrade.service.impl;

import com.marvin.campustrade.common.IncludeInactiveUsers;
import com.marvin.campustrade.constants.RequestType;
import com.marvin.campustrade.constants.TokenType;
import com.marvin.campustrade.data.dto.ImageDTO;
import com.marvin.campustrade.data.dto.ProfileImageDTO;
import com.marvin.campustrade.data.dto.auth.*;
import com.marvin.campustrade.data.dto.user.*;
import com.marvin.campustrade.data.entity.*;
import com.marvin.campustrade.data.mapper.BlockMapper;
import com.marvin.campustrade.data.mapper.ProfileMapper;
import com.marvin.campustrade.data.mapper.TransactionMapper;
import com.marvin.campustrade.data.mapper.UserMapper;
import com.marvin.campustrade.exception.*;
import com.marvin.campustrade.repository.*;
import com.marvin.campustrade.service.EmailService;
import com.marvin.campustrade.service.ImageService;
import com.marvin.campustrade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final ProfileMapper  profileMapper;
    private final UsersBlockRepository usersBlockRepository;
    private final BlockMapper blockMapper;
    private final TransactionRepository  transactionRepository;
    private final TransactionMapper  transactionMapper;
    private final ProductRepository  productRepository;
    private final FavouriteRepository  favouriteRepository;
    private final ImageService imageService;

    @Override
    @IncludeInactiveUsers
    @Transactional
    public UserResponse createUser(RegisterRequest request) {

        Optional<Users> existingUserOpt = userRepository.findByEmail(request.getEmail());

        Users user;

        if (existingUserOpt.isPresent()) {
            user = existingUserOpt.get();

            if (Boolean.TRUE.equals(user.getIsActive())) {
                throw new EmailAlreadyExistsException("Email already exists");
            }
            user.setIsActive(true);
            user.setIsVerified(false);
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        } else {
            user = userMapper.toEntity(request);
            user.setIsActive(true);
            user.setIsVerified(false);
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        // Load university
        University university = universityRepository.findByName(request.getUniversity())
                .orElseThrow(() -> new UniversityNotFoundException("University not found"));

        // Validate email domain
        String email = request.getEmail();
        String requiredDomain = university.getDomain();

        if (!email.toLowerCase().endsWith("@" + requiredDomain.toLowerCase())) {
            throw new InvalidStudentEmailDomainException(
                    "Email must be a student email ending with @" + requiredDomain
            );
        }

        user.setUniversity(university);

        userRepository.save(user);

        String code = String.format("%06d", new Random().nextInt(999999));
        Token token = new Token();
        token.setContent(code);
        token.setType(TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        token.setUser(user);
        tokenRepository.save(token);

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

        String code = String.format("%06d", new Random().nextInt(1_000_000));

        Token token = tokenRepository
                .findByUserAndType(user, TokenType.PASSWORD_RESET)
                .orElseGet(() -> {
                    Token t = new Token();
                    t.setUser(user);
                    t.setType(TokenType.PASSWORD_RESET);
                    return t;
                });

        token.setContent(code);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));

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
    public void resendVerificationEmail(ResendVerificationCodeDTO request){
        Users user  = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Token> token = tokenRepository.findByUserAndType(user, TokenType.EMAIL_VERIFICATION);
        if(token.isPresent()){
            String code = String.format("%06d", new Random().nextInt(999999));
            token.get().setContent(code);
            token.get().setExpiresAt(LocalDateTime.now().plusMinutes(15));
            tokenRepository.save(token.get());
            emailService.sendVerificationEmail(user.getEmail(), token.get());
        }
        else{
            String code = String.format("%06d", new Random().nextInt(999999));
            Token newToken = new Token();
            newToken.setContent(code);
            newToken.setType(TokenType.EMAIL_VERIFICATION);
            newToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            newToken.setUser(user);
            tokenRepository.save(newToken);
            emailService.sendVerificationEmail(user.getEmail(), newToken);
        }
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
        if(request.getType() == RequestType.CHANGE_PASSWORD){
            Users user = getCurrentUser();
            if(!(user.getIsVerified() && user.getIsActive())){
                throw new RuntimeException("User is not verified");
            }
            if(Objects.equals(request.getOldPassword(), request.getNewPassword())){
                throw new RuntimeException("New password can not be same with old password");
            }
            if(!Objects.equals(request.getNewPassword(), request.getConfirmNewPassword())){
                throw new RuntimeException("New passwords do not match");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public UserResponse getCurrentProfile(){
        Users user = getCurrentUser();
        if(!(user.getIsVerified() && user.getIsActive())){
            throw new RuntimeException("User is not valid for this function");
        }
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse editProfile(EditProfileRequest request){
        System.out.println("Editing profile: " + request);
        Users user = getCurrentUser();
        System.out.println("Current user: " + user);
        if(!(user.getIsVerified() && user.getIsActive())){
            throw new RuntimeException("User is not valid for this function");
        }
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDescription(request.getDescription());
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteProfile() {

        Users user = getCurrentUser();

        if (!(user.getIsVerified() && user.getIsActive())) {
            throw new UnauthorizedActionException("User is not valid for this function");
        }

        List<Product> products = productRepository.findAllByUserId(user.getId());
        productRepository.deleteAll(products);

        List<Favourite> favourites = favouriteRepository.findAllByUser(user);
        favouriteRepository.deleteAll(favourites);

        usersBlockRepository.deleteAllByBlocker(user);
        usersBlockRepository.deleteAllByBlocked(user);
        tokenRepository.deleteAllByUser(user);
        user.setIsActive(false);
        userRepository.delete(user);
    }


    @Override
    public ProfileResponse getUser(String id) {

        Users viewer = getCurrentUser();
        if (!viewer.getIsActive() || !viewer.getIsVerified()) {
            throw new RuntimeException("You are not allowed to view profiles");
        }

        Long userId = Long.parseLong(id);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive() || !user.getIsVerified()) {
            throw new RuntimeException("This profile is not available");
        }

        if(usersBlockRepository.findByBlockerAndBlocked(viewer, user).isPresent()){
            throw new BlockedByException("You blocked the user");
        }

        if(usersBlockRepository.findByBlockerAndBlocked(user, viewer).isPresent()){
            throw new BlockedByException("You are blocked");
        }
        return profileMapper.toResponse(user);
    }

    @Override
    public BlockResponse blockUser(String id) {
        Users blocker = getCurrentUser();

        if (!blocker.getIsActive() || !blocker.getIsVerified()) {
            throw new RuntimeException("You are not allowed to block profiles");
        }

        Long userId = Long.parseLong(id);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive() || !user.getIsVerified()) {
            throw new RuntimeException("This profile is not available");
        }

        if (usersBlockRepository.findByBlockerAndBlocked(blocker, user).isPresent()) {
            throw new RuntimeException("This user is already blocked.");
        }

        UsersBlock blocking = new UsersBlock();
        blocking.setBlocker(blocker);
        blocking.setBlocked(user);
        usersBlockRepository.save(blocking);

        return blockMapper.toBlock(user);
    }

    @Override
    public BlockListResponse getBlockList() {
        Users user = getCurrentUser();
        if(!(user.getIsVerified() && user.getIsActive())){
            throw new RuntimeException("User is not valid for this function");
        }
        List<Users> blocked = usersBlockRepository.findBlockedUsers(user)
                .orElseThrow(() -> new BlockedByException("No blocked users"));

        List<UserResponse> blockeduser = new ArrayList<>();

        for(Users users : blocked){
            UserResponse userResponse = userMapper.toResponse(users);
            blockeduser.add(userResponse);
        }

        return BlockListResponse.builder().userList(blockeduser).numberOfBlocked(blockeduser.size()).build();
    }

    @Override
    public BlockResponse unblockUser(String id) {
        Users blocker = getCurrentUser();

        if (!blocker.getIsActive() || !blocker.getIsVerified()) {
            throw new RuntimeException("You are not allowed to block profiles");
        }

        Long userId = Long.parseLong(id);

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive() || !user.getIsVerified()) {
            throw new RuntimeException("This profile is not available");
        }

        UsersBlock blocking = usersBlockRepository.findByBlockerAndBlocked(blocker, user)
                .orElseThrow(() -> new RuntimeException("This user is not blocked"));

        usersBlockRepository.delete(blocking);

        return blockMapper.toBlock(user);
    }

    @Override
    public SalesResponseDTO getSalesHistory() {

        Users currentUser = getCurrentUser();

        List<Transactions> salesList =
                transactionRepository.findTransactionBySellerId(currentUser.getId())
                        .orElseThrow(() -> new RuntimeException("You did not sold any item!"));

        List<TransactionDTO> transactionDTOs =
                transactionMapper.toDtoList(salesList);

        return SalesResponseDTO.builder()
                .transactions(transactionDTOs)
                .build();
    }

    @Override
    public PurchaseResponseDTO getPurchaseHistory() {

        Users currentUser = getCurrentUser();

        List<Transactions> purchaseList =
                transactionRepository.findTransactionByBuyerId(currentUser.getId())
                        .orElseThrow(() -> new RuntimeException("You did not purchase any item!"));

        List<TransactionDTO> transactionDTOs =
                transactionMapper.toDtoList(purchaseList);

        return PurchaseResponseDTO.builder()
                .transactions(transactionDTOs)
                .build();
    }

    @Override
    public ProfileImageDTO.PresignResponse presignProfilePicture(ProfileImageDTO.PresignRequest request){
        Users user = getCurrentUser();

        ImageDTO.PresignedImage presigned =
                imageService.presignSingleUpload(
                        "profile-pictures/" + user.getId() + "/",
                        request.getContentType()
                );

        return new ProfileImageDTO.PresignResponse(
                presigned.getKey(),
                presigned.getUploadUrl()
        );
    }

    @Override
    public void saveProfilePicture(ProfileImageDTO.SaveRequest request) {
        Users user = getCurrentUser();

        String oldKey = user.getProfilePicUrl();

        user.setProfilePicUrl(request.getKey());    // key not url
        userRepository.save(user);

        //delete old image from s3
        if(oldKey != null && !oldKey.equals(request.getKey())){
            imageService.deleteByKey(oldKey);
        }
    }

    public ProfileImageDTO.ViewResponse getUserProfilePicture(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getProfilePicUrl() == null) {
            return new ProfileImageDTO.ViewResponse(null);
        }

        String url = imageService.presignGet(user.getProfilePicUrl());
        return new ProfileImageDTO.ViewResponse(url);
    }

    @Override
    public ProfileImageDTO.ViewResponse getMyProfilePicture() {
        Users user = getCurrentUser();

        if(user.getProfilePicUrl() == null){
            return new ProfileImageDTO.ViewResponse(null);
        }

        String url = imageService.presignGet(user.getProfilePicUrl());

        return new ProfileImageDTO.ViewResponse(url);
    }

    ////hilal filter testi silebilirsiniz
    //includeInactive'i koymadım yani active olmayan user hata vermeli
    @Override
    public UserResponse findActiveUserByEamil(String email){
        return userMapper.toResponse(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));
    }

    //active olmayan user dönmeli
    @Override
    @IncludeInactiveUsers
    @Transactional //have to be transactional because of the custom aspect (IncludeInactiveUsers)
    public UserResponse findInActiveUserByEmail(String email){
        return userMapper.toResponse(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));
    }
}
