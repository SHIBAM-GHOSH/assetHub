package com.assetHub.dto.user;

import com.assetHub.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}