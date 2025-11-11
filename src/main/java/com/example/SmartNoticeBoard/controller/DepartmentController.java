package com.example.SmartNoticeBoard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SmartNoticeBoard.DTO.DepartmentDTO;
import com.example.SmartNoticeBoard.service.DepartmentService;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	@GetMapping("/getAllDepartments")
	public List<DepartmentDTO> getAllDepartments() {
		return departmentService.getAllDepartments();
	}
}
