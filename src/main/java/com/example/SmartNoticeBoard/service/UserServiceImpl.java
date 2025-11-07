package com.example.SmartNoticeBoard.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SmartNoticeBoard.DTO.AuthResponseDTO;
import com.example.SmartNoticeBoard.DTO.UserDto;
import com.example.SmartNoticeBoard.model.Role;
import com.example.SmartNoticeBoard.model.User;
import com.example.SmartNoticeBoard.repository.RoleRepository;
import com.example.SmartNoticeBoard.repository.UserRepository;
import com.example.SmartNoticeBoard.security.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil = new JwtUtil();

	private UserDto mapToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setRoles(user.getRoles());
		dto.setDepartment(user.getDepartment());
		dto.setYear(user.getYear());
		return dto;
	}

	private User mapToEntity(UserDto dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword()); // hashed password
		if (dto.getRoles() != null) {
			Set<Role> roles = dto.getRoles().stream().map(r -> roleRepository.findByName(r.getName()))
					.collect(Collectors.toSet());
			user.setRoles(roles);
		}
		user.setDepartment(dto.getDepartment());
		user.setYear(dto.getYear());
		return user;
	}

	@Override
	public UserDto register(UserDto userDto) {
		if (userRepository.findByUsername(userDto.getUsername()) != null) {
			throw new RuntimeException("User already exists");
		}
		User saved = userRepository.save(mapToEntity(userDto));
		return mapToDto(saved);
	}


	@Override
	public AuthResponseDTO generateToken(String username, String password) {
		User user = userRepository.findByUsername(username);
		if (user == null || !user.getPassword().equals(password)) {
			throw new RuntimeException("Invalid credentials");
		}
		String accessToken = jwtUtil.generateToken(user.getUsername());
		String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

		return new AuthResponseDTO(accessToken, refreshToken);
	}

	@Override
	public UserDto login(String username, String password) {
		User user = userRepository.findByUsername(username);
		if (user == null || !user.getPassword().equals(password)) {
			throw new RuntimeException("Invalid credentials");
		}
		return mapToDto(user);
	}
	
    @Override
    public List<UserDto> getAllTeachersAndAdmins() {
        List<String> roles = List.of("ADMIN", "TEACHER");
        List<User> users = userRepository.findByRoleNames(roles);

        return users.stream().map(this::mapToDto).collect(Collectors.toList());
    }
}
