package com.example.SmartNoticeBoard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SmartNoticeBoard.DTO.RoleDTO;
import com.example.SmartNoticeBoard.service.RoleService;

@RestController
@RequestMapping("/api/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping("/getAllRoles")
	public List<RoleDTO> getAllRoles() {
		return roleService.getAllRoles();
	}
}
