package com.example.SmartNoticeBoard.DTO;

import java.util.Set;

import com.example.SmartNoticeBoard.model.Role;

public class UserDto {
	private Long id;
	private String username;
    private String password;   // ✅ Needed for registration & login
	private Set<Role> roles;
	private String department;
	private Integer year;
	private String token;
	private String name;
	private String mobileNumber;
	private String dateOfBirth;
	private String gmail;
	private boolean firstLogin;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getDateOfBirth() {
	    return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
	    this.dateOfBirth = dateOfBirth;
	}
	public String getGmail() {
		return gmail;
	}
	public void setGmail(String gmail) {
		this.gmail = gmail;
	}
	
	public boolean isFirstLogin() { 
		return firstLogin;
	}
	
	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin; 
	}

}
