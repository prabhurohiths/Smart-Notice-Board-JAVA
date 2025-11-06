package com.example.SmartNoticeBoard.DTO;

import java.util.List;

public class NoticeDto {
	private Long id;
	private String title;
	private String description;
	private String department;
	private Integer year;
	private String postedBy;
	private String postedDate;
	
	private List<String> imagePaths;

	public List<String> getImagePaths() {
	    return imagePaths;
	}

	public void setImagePaths(List<String> imagePaths) {
	    this.imagePaths = imagePaths;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}


	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(String postedBy) {
		this.postedBy = postedBy;
	}

	public String getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}

}
