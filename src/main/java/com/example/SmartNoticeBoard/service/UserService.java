package com.example.SmartNoticeBoard.service;

import java.util.List;
import java.util.Map;

import com.example.SmartNoticeBoard.DTO.AuthResponseDTO;
import com.example.SmartNoticeBoard.DTO.UserDto;

public interface UserService {

    // Generate JWT token (separate API)
    AuthResponseDTO generateToken(String username, String password);

    // Register new user
    UserDto register(UserDto userDto);

    // Login user (returns user info)
    UserDto login(String username, String password);
    
    void resetPassword(String username, String newPassword);
    
    List<UserDto> getAllTeachersAndAdmins();
    
    
    
    
    Map<String, Object> getAllUsers(int page, int size);

    UserDto getUserById(Long id);
    void updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    
    boolean usernameExists(String username);

}

