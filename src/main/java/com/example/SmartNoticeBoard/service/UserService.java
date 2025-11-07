package com.example.SmartNoticeBoard.service;

import java.util.List;

import com.example.SmartNoticeBoard.DTO.AuthResponseDTO;
import com.example.SmartNoticeBoard.DTO.UserDto;

public interface UserService {

    // Generate JWT token (separate API)
    AuthResponseDTO generateToken(String username, String password);

    // Register new user
    UserDto register(UserDto userDto);

    // Login user (returns user info)
    UserDto login(String username, String password);
    
    List<UserDto> getAllTeachersAndAdmins();
}

