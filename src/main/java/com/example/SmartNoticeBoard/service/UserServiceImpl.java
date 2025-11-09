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
import com.example.SmartNoticeBoard.util.AESUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil = new JwtUtil();

	// ✅ Convert Entity → DTO
	private UserDto mapToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setName(user.getName());
		dto.setMobileNumber(user.getMobileNumber());
		dto.setGmail(user.getGmail());
		dto.setRoles(user.getRoles());
		dto.setDepartment(user.getDepartment());
		dto.setYear(user.getYear());
		dto.setFirstLogin(user.isFirstLogin());
		return dto;
	}

	// ✅ Convert DTO → Entity
	private User mapToEntity(UserDto dto) {
		User user = new User();
		user.setId(dto.getId());
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword()); // stored as plain/hased
		user.setName(dto.getName());
		user.setMobileNumber(dto.getMobileNumber());
		user.setGmail(dto.getGmail());
		user.setDepartment(dto.getDepartment());
		user.setYear(dto.getYear());

		if (dto.getRoles() != null) {
			Set<Role> roles = dto.getRoles().stream()
					.map(r -> roleRepository.findByName(r.getName()))
					.collect(Collectors.toSet());
			user.setRoles(roles);
		}
		return user;
	}

	@Override
	public UserDto register(UserDto userDto) {
	    if (userRepository.findByUsername(userDto.getUsername()) != null) {
	        throw new RuntimeException("User already exists");
	    }

	    // 1️⃣ Decrypt AES password from frontend
	    String decryptedPassword = AESUtil.decrypt(userDto.getPassword());

	    // 2️⃣ Hash with BCrypt
	    String hashedPassword = passwordEncoder.encode(decryptedPassword);

	    // 3️⃣ Map and save
	    User user = mapToEntity(userDto);
	    user.setPassword(hashedPassword); // store hashed password
	    user.setFirstLogin(true); // mark as first login if you need that
	    User saved = userRepository.save(user);

	    return mapToDto(saved);
	}



	@Override
	public AuthResponseDTO generateToken(String username, String password) {
	    String decryptedPassword = AESUtil.decrypt(password);
	    User user = userRepository.findByUsername(username);

	    if (user == null || !passwordEncoder.matches(decryptedPassword, user.getPassword())) {
	        throw new RuntimeException("Invalid credentials");
	    }

	    String accessToken = jwtUtil.generateToken(user.getUsername());
	    String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());
	    return new AuthResponseDTO(accessToken, refreshToken);
	}

	@Override
	public UserDto login(String username, String password) {
	    String decryptedPassword = AESUtil.decrypt(password);
	    User user = userRepository.findByUsername(username);

	    if (user == null || !passwordEncoder.matches(decryptedPassword, user.getPassword())) {
	        throw new RuntimeException("Invalid credentials");
	    }

	    return mapToDto(user);
	}
	
	@Override
	public void resetPassword(String username, String newPassword) {
	    User user = userRepository.findByUsername(username);
	    if (user == null) throw new RuntimeException("User not found");

	    String decrypted = AESUtil.decrypt(newPassword);
	    user.setPassword(passwordEncoder.encode(decrypted));
	    user.setFirstLogin(false);
	    userRepository.save(user);
	}

	
    @Override
    public List<UserDto> getAllTeachersAndAdmins() {
        List<String> roles = List.of("ADMIN", "TEACHER");
        List<User> users = userRepository.findByRoleNames(roles);

        return users.stream().map(this::mapToDto).collect(Collectors.toList());
    }
}
