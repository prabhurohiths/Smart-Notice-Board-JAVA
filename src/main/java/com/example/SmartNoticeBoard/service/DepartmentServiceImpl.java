package com.example.SmartNoticeBoard.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SmartNoticeBoard.DTO.DepartmentDTO;
import com.example.SmartNoticeBoard.model.Department;
import com.example.SmartNoticeBoard.repository.DepartmentRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Override
	public List<DepartmentDTO> getAllDepartments() {
		List<Department> departments = departmentRepository.findAll();
		return departments.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	// Convert Entity → DTO
	private DepartmentDTO mapToDto(Department department) {
		DepartmentDTO dto = new DepartmentDTO();
		dto.setId(department.getId());
		dto.setName(department.getName());
		return dto;
	}
}
