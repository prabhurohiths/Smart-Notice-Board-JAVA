package com.example.SmartNoticeBoard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "year")
public class Year {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private Integer yearNumber; // e.g., 1, 2, 3, 4, 0 (All)

	@Column(nullable = false)
	private String yearName; // e.g., "1st Year", "2nd Year", "3rd Year", etc.

	public Year() {
	}

	public Year(Integer yearNumber, String yearName) {
		this.yearNumber = yearNumber;
		this.yearName = yearName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(Integer yearNumber) {
		this.yearNumber = yearNumber;
	}

	public String getYearName() {
		return yearName;
	}

	public void setYearName(String yearName) {
		this.yearName = yearName;
	}
}
