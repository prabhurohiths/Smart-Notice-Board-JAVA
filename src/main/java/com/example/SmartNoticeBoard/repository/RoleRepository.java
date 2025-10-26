package com.example.SmartNoticeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SmartNoticeBoard.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
	