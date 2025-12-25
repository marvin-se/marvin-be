package com.marvin.campustrade.data.dto.user;

import com.marvin.campustrade.data.dto.auth.UserResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BlockListResponse {
    private List<UserResponse> userList;
    private int numberOfBlocked;
}
