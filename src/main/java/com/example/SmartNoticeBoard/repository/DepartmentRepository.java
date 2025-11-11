package com.example.SmartNoticeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SmartNoticeBoard.model.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
	Department findByName(String name);
}
