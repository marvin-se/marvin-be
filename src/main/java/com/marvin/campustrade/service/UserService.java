package com.marvin.campustrade.service;

import com.marvin.campustrade.data.dto.auth.RegisterRequest;
import com.marvin.campustrade.data.entity.Users;

public interface UserService {
    Users createUser(RegisterRequest request);
}
