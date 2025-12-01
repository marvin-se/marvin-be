package com.marvin.campustrade.service.impl;


import com.marvin.campustrade.data.dto.UserDTO;
import com.marvin.campustrade.data.entity.Users;
import com.marvin.campustrade.data.mapper.ApplicationMapper;
import com.marvin.campustrade.repository.UserRepository;
import com.marvin.campustrade.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO getUserById(Long userId) {
        Users user= userRepository.findById(userId)
                .orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with ID " + userId + " not found")
                );

        return ApplicationMapper.INSTANCE.toUserDTO(user);
    }

}
