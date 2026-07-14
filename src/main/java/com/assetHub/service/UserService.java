package com.assetHub.service;

import com.assetHub.dto.user.UserRegistrationRequest;
import com.assetHub.dto.user.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);
}