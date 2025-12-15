package com.marvin.campustrade.controller;

import com.marvin.campustrade.data.dto.auth.UserResponse;
import com.marvin.campustrade.data.dto.user.BlockResponse;
import com.marvin.campustrade.data.dto.user.EditProfileRequest;
import com.marvin.campustrade.data.dto.user.ProfileResponse;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me")
    //Turning User Response with full info because without token this info can not be seen, so
    //I do not think there is a security risk here
    public ResponseEntity<UserResponse> getCurrentProfile() {
        return ResponseEntity.ok(userService.getCurrentProfile());
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<UserResponse> editProfile(@Valid @RequestBody EditProfileRequest request) {
        return ResponseEntity.ok(userService.editProfile(request));
    }

    @DeleteMapping("/delete-profile")
    public ResponseEntity<String> deleteProfile() {
        userService.deleteProfile();
        return ResponseEntity.ok("Your profile has been deleted");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<BlockResponse> blockUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userService.blockUser(userId));
    }

    @DeleteMapping("/{userId}/unblock")
    public ResponseEntity<BlockResponse> unblockUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userService.unblockUser(userId));
    }

}
