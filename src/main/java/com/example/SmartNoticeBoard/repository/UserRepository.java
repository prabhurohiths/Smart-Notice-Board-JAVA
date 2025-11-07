package com.example.SmartNoticeBoard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.SmartNoticeBoard.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
	
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name IN :roles")
    List<User> findByRoleNames(@Param("roles") List<String> roles);
}
