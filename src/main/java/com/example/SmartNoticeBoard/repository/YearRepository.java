package com.example.SmartNoticeBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SmartNoticeBoard.model.Year;

public interface YearRepository extends JpaRepository<Year, Long> {
	Year findByYearNumber(Integer yearNumber);
}
