package com.example.SmartNoticeBoard.DTO;

public class YearDto {
	private Long id;
	private Integer yearNumber;
	private String yearName;

	public YearDto() {
	}

	public YearDto(Long id, Integer yearNumber, String yearName) {
		this.id = id;
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
