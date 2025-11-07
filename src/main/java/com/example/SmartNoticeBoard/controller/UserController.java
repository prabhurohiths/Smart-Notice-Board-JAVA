package com.example.SmartNoticeBoard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.SmartNoticeBoard.DTO.AuthResponseDTO;
import com.example.SmartNoticeBoard.DTO.UserDto;
import com.example.SmartNoticeBoard.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

//	private final UserService userService = new UserService();
//
//	@PostMapping("/login")
//	public AuthResponseDTO login(@RequestParam String username, @RequestParam String password) {
//		AuthResponseDTO authResponseDTO = userService.login(username, password);
//		return authResponseDTO;
//	}
//
//	@PostMapping("/refresh")
//	public AuthResponseDTO refreshToken(@RequestParam String refreshToken) {
//		AuthResponseDTO authResponseDTO = userService.refreshToken(refreshToken);
//		return authResponseDTO;
//	}

	// -------------------------------------------------------------------------------

	@Autowired
	private UserService userService;

	// Admin → Register user (Teacher/Student)
	@PostMapping("/register")
	public UserDto register(@RequestBody UserDto userDto) {
		return userService.register(userDto);
	}

	@PostMapping("/token")
	public AuthResponseDTO token(@RequestParam String username, @RequestParam String password) {
		AuthResponseDTO authResponseDTO = userService.generateToken(username, password);
		return authResponseDTO;
	}

	// All → Login
	@PostMapping("/login")
	public UserDto login(@RequestBody UserDto userDto) {
		return userService.login(userDto.getUsername(), userDto.getPassword());
	}
	
    // ✅ Get all Admin and Teacher users
    @GetMapping("/getAllTeachersAndAdmins")
    public List<UserDto> getAllTeachersAndAdmins() {
        return userService.getAllTeachersAndAdmins();
    }

}
