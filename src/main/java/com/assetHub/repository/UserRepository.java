package com.assetHub.repository;

import com.assetHub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> 
    {

        boolean existsByEmail(String email);
    }