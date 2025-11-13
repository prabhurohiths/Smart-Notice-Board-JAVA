package com.example.SmartNoticeBoard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.SmartNoticeBoard.model.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
	
	List<Notice> findAllByDepartment_NameAndYear_YearNumber(String department, Integer year);
	
	@Query("""
	        SELECT n FROM Notice n
	        WHERE (:postedBy IS NULL OR LOWER(n.postedBy.username) = LOWER(:postedBy))
	          AND (:year IS NULL OR n.year.yearNumber = :year)
	          AND (:uploadedYear IS NULL OR FUNCTION('YEAR', n.postedDate) = :uploadedYear)
	          AND (:department IS NULL OR LOWER(n.department.name) = LOWER(:department))
	        ORDER BY n.postedDate DESC
	    """)
	    Page<Notice> filterNoticesFromDb(
	        @Param("postedBy") String postedBy,
	        @Param("year") Integer year,
	        @Param("uploadedYear") Integer uploadedYear,
	        @Param("department") String department,
	        Pageable pageable
	    );
}
