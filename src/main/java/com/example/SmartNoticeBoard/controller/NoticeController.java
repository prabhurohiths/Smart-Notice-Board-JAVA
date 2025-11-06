package com.example.SmartNoticeBoard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.SmartNoticeBoard.DTO.NoticeDto;
import com.example.SmartNoticeBoard.service.NoticeService;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	// Admin/Teacher → Create Notice
//	@PostMapping("/createNotice")
//	public NoticeDto createNotice(@RequestBody NoticeDto noticeDto, @RequestParam Long postedById) {
//		return noticeService.createNotice(noticeDto, postedById);
//	}
	
	@PostMapping(value = "/createNotice", consumes = { "multipart/form-data" })
	public NoticeDto createNotice(
	        @RequestPart("notice") NoticeDto noticeDto,
	        @RequestPart(value = "images", required = false) List<MultipartFile> images,
	        @RequestParam Long postedById) {

	    return noticeService.createNotice(noticeDto, postedById, images);
	}

	// Admin/Teacher → Update Notice
	@PutMapping("/{id}")
	public NoticeDto updateNotice(@PathVariable Long id, @RequestBody NoticeDto noticeDto) {
		return noticeService.updateNotice(id, noticeDto);
	}

	// Admin → Delete Notice
	@DeleteMapping("/{id}")
	public void deleteNotice(@PathVariable Long id) {
		noticeService.deleteNotice(id);
	}

	// Admin-> View All Notices
	@GetMapping("/getAllNotices")
	public List<NoticeDto> getAllNotices() {
		return noticeService.getAllNotices();
	}

	
    @GetMapping("/getStudentNotices")
    public List<NoticeDto> getStudentNotices(
            @RequestParam String department,
            @RequestParam Integer year) {

        return noticeService.getNoticesForStudent(department, year);
    }


//	// Student → Filter Notices by Department
//	@GetMapping("/department/{department}")
//	public List<NoticeDto> getNoticesByDepartment(@PathVariable String department) {
//		return noticeService.getNoticesByDepartment(department);
//	}
}
