package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.UserDTO;

public interface UserService {

    UserDTO getUserById(Long userId);
}
