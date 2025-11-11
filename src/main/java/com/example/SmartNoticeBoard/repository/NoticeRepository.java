package com.example.SmartNoticeBoard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SmartNoticeBoard.model.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	List<Notice> findAllByDepartment_NameAndYear_YearNumber(String department, Integer year);
}
