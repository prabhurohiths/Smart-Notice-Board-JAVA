package com.example.SmartNoticeBoard.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SmartNoticeBoard.DTO.AuthResponseDTO;
import com.example.SmartNoticeBoard.DTO.UserDto;
import com.example.SmartNoticeBoard.model.Department;
import com.example.SmartNoticeBoard.model.Role;
import com.example.SmartNoticeBoard.model.User;
import com.example.SmartNoticeBoard.model.Year;
import com.example.SmartNoticeBoard.repository.DepartmentRepository;
import com.example.SmartNoticeBoard.repository.RoleRepository;
import com.example.SmartNoticeBoard.repository.UserRepository;
import com.example.SmartNoticeBoard.repository.YearRepository;
import com.example.SmartNoticeBoard.security.JwtUtil;
import com.example.SmartNoticeBoard.util.AESUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private YearRepository yearRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final JwtUtil jwtUtil = new JwtUtil();

	// Convert Entity → DTO
	private UserDto mapToDto(User user) {
		UserDto dto = new UserDto();
		dto.setId(user.getId());
		dto.setUsername(user.getUsername());
		dto.setName(user.getName());
		dto.setMobileNumber(user.getMobileNumber());
	    dto.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
		dto.setGmail(user.getGmail());
		dto.setRoles(user.getRoles());
		dto.setDepartment(user.getDepartment() != null ? user.getDepartment().getName() : null);
		dto.setYear(user.getYear() != null ? user.getYear().getYearNumber() : null);
		dto.setFirstLogin(user.isFirstLogin());
		return dto;
	}

	// Convert DTO → Entity
	private User mapToEntity(UserDto dto) {
	    User user = new User();
	    user.setId(dto.getId());
	    user.setUsername(dto.getUsername());
	    user.setPassword(dto.getPassword()); 
	    user.setName(dto.getName());
	    user.setMobileNumber(dto.getMobileNumber());
	    user.setGmail(dto.getGmail());

	    // Handle Role Conversion
	    Set<Role> roles = dto.getRoles().stream()
	            .map(r -> roleRepository.findByName(r.getName()))
	            .collect(Collectors.toSet());
	    user.setRoles(roles);

	    // Check if user is ADMIN or TEACHER
	    boolean isAdminOrTeacher = roles.stream()
	            .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN") || r.getName().equalsIgnoreCase("TEACHER"));

	    // Department – only for STUDENT
	    if (!isAdminOrTeacher && dto.getDepartment() != null && !dto.getDepartment().isBlank()) {
	        Department dept = departmentRepository.findByName(dto.getDepartment());
	        if (dept == null) {
	            throw new RuntimeException("Invalid department: " + dto.getDepartment());
	        }
	        user.setDepartment(dept);
	    } else {
	        user.setDepartment(null);
	    }

	    // Year – only for STUDENT
	    if (!isAdminOrTeacher && dto.getYear() != null) {
	        Year yearLevel = yearRepository.findByYearNumber(dto.getYear());
	        if (yearLevel == null) {
	            throw new RuntimeException("Invalid year: " + dto.getYear());
	        }
	        user.setYear(yearLevel);
	    } else {
	        user.setYear(null);
	    }

	    if (dto.getDateOfBirth() != null) {
	        user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
	    }

	    return user;
	}


	@Override
	public UserDto register(UserDto userDto) {
	    if (userRepository.findByUsername(userDto.getUsername()) != null) {
	        throw new RuntimeException("User already exists");
	    }

	    // Decrypt AES password from frontend
	    String decryptedPassword = AESUtil.decrypt(userDto.getPassword());

	    // Hash with BCrypt
	    String hashedPassword = passwordEncoder.encode(decryptedPassword);

	    // Map and save
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
    
    
    
    
    
    @Override
    public Map<String, Object> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserDto> userDtos = userPage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("users", userDtos);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        return response;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return mapToDto(user);
    }

    @Override
    public void updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Update basic editable fields
        existingUser.setName(userDto.getName());
        existingUser.setGmail(userDto.getGmail());
        existingUser.setMobileNumber(userDto.getMobileNumber());

        // Update date of birth if present
        if (userDto.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(LocalDate.parse(userDto.getDateOfBirth()));
        }

        // Update roles if provided
        Set<Role> roles = existingUser.getRoles(); // default existing roles
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            roles = userDto.getRoles().stream()
                    .map(r -> roleRepository.findByName(r.getName()))
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        // Check if user is ADMIN or TEACHER
        boolean isAdminOrTeacher = roles.stream()
                .anyMatch(r -> r.getName().equalsIgnoreCase("ADMIN") || r.getName().equalsIgnoreCase("TEACHER"));

        if (isAdminOrTeacher) {
            // For Admin or Teacher → clear department and year
            existingUser.setDepartment(null);
            existingUser.setYear(null);
        } else {
            // For Student → update department and year normally
            // Department
            if (userDto.getDepartment() != null && !userDto.getDepartment().isBlank()) {
                Department dept = departmentRepository.findByName(userDto.getDepartment());
                if (dept == null) {
                    dept = departmentRepository.save(new Department(userDto.getDepartment()));
                }
                existingUser.setDepartment(dept);
            } else {
                existingUser.setDepartment(null);
            }

            // Year
            if (userDto.getYear() != null) {
                Year yearLevel = yearRepository.findByYearNumber(userDto.getYear());
                if (yearLevel == null) {
                    throw new RuntimeException("Invalid year: " + userDto.getYear());
                }
                existingUser.setYear(yearLevel);
            } else {
                existingUser.setYear(null);
            }
        }

        userRepository.save(existingUser);
    }


    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }


}
