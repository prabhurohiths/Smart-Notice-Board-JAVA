package com.example.SmartNoticeBoard.DTO;

import java.time.LocalDateTime;
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
	private String modifiedBy;
	private String modifiedDate;
	private List<String> imageFileNames;

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

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<String> getImageFileNames() {
		return imageFileNames;
	}

	public void setImageFileNames(List<String> imageFileNames) {
		this.imageFileNames = imageFileNames;
	}

}
