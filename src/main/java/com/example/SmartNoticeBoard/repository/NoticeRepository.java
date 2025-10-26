package com.example.SmartNoticeBoard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SmartNoticeBoard.model.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByDepartmentAndBranchAndYearAndSection(
        String department, String branch, Integer year, String section
    );
}