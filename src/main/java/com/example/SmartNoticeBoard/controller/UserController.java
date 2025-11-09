package com.example.SmartNoticeBoard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SmartNoticeBoard.DTO.AuthResponseDTO;
import com.example.SmartNoticeBoard.DTO.UserDto;
import com.example.SmartNoticeBoard.service.UserService;
import com.example.SmartNoticeBoard.util.PasswordHashGenerator;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	// Admin → Register user (Teacher/Student)
	@PostMapping("/register")
	public UserDto register(@RequestBody UserDto userDto) {
		return userService.register(userDto);
	}

	@PostMapping("/token")
	public AuthResponseDTO token(@RequestParam String username, @RequestParam String password) {
		PasswordHashGenerator.generatePassword();
		AuthResponseDTO authResponseDTO = userService.generateToken(username, password);
		return authResponseDTO;
	}

	// All → Login
	@PostMapping("/login")
	public UserDto login(@RequestBody UserDto userDto) {
		return userService.login(userDto.getUsername(), userDto.getPassword());
	}
	
	@PostMapping("/resetPassword")
	public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
	    userService.resetPassword(username, newPassword);
	    Map<String, String> response = new HashMap<>();
	    response.put("message", "Password updated successfully");
	    return ResponseEntity.ok(response);
	}
	
    // ✅ Get all Admin and Teacher users
    @GetMapping("/getAllTeachersAndAdmins")
    public List<UserDto> getAllTeachersAndAdmins() {
        return userService.getAllTeachersAndAdmins();
    }

}
