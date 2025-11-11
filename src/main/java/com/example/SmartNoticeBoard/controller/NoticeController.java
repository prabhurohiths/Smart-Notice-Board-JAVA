package com.example.SmartNoticeBoard.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.SmartNoticeBoard.DTO.NoticeDto;
import com.example.SmartNoticeBoard.model.User;
import com.example.SmartNoticeBoard.repository.UserRepository;
import com.example.SmartNoticeBoard.security.JwtUtil;
import com.example.SmartNoticeBoard.service.NoticeService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	// Admin/Teacher → Create Notice
	@PostMapping(value = "/createNotice", consumes = { "multipart/form-data" })
	public NoticeDto createNotice(@RequestPart("notice") NoticeDto noticeDto,
			@RequestPart(value = "images", required = false) List<MultipartFile> images,
			@RequestParam Long postedById) {

		return noticeService.createNotice(noticeDto, postedById, images);
	}

	// Admin → Delete Notice
	@DeleteMapping("/{id}")
	public void deleteNotice(@PathVariable Long id) {
		noticeService.deleteNotice(id);
	}

	@DeleteMapping("/deleteNotice/{id}")
	public List<NoticeDto> deleteNotice(@PathVariable Long id, @RequestParam Long userId) {
		noticeService.deleteNoticeWithRoleCheck(id, userId);
		return noticeService.getAllNotices();
	}

	@GetMapping("/getNoticeById/{id}")
	public NoticeDto getNoticeById(@PathVariable Long id) {
		return noticeService.getNoticeById(id);
	}

	// Admin-> View All Notices
	@GetMapping("/getAllNotices")
	public List<NoticeDto> getAllNotices() {
		return noticeService.getAllNotices();
	}

	// Admin/Teacher → Update Notice
	@PutMapping("/updateNoticeWithImages/{id}")
	public NoticeDto updateNoticeWithImages(@PathVariable Long id, @RequestPart("notice") NoticeDto noticeDto,
			@RequestPart(value = "files", required = false) List<MultipartFile> files,

			HttpServletRequest request) {

		// ✅ Extract username from JWT
		String authHeader = request.getHeader("Authorization");
		String username = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			username = jwtUtil.extractUsername(authHeader.substring(7));
		}

		// ✅ Get user details from DB
		if (username != null) {
			User user = userRepository.findByUsername(username);
			if (user != null) {
				noticeDto.setModifiedBy(user.getUsername()); // store username of logged user
			}
		}

		return noticeService.updateNoticeWithImages(id, noticeDto, files);
	}

	@GetMapping("/getStudentNotices")
	public List<NoticeDto> getStudentNotices(@RequestParam String department, @RequestParam Integer year) {
		return noticeService.getNoticesForStudent(department, year);
	}

	@GetMapping("/filterNotices")
	public List<NoticeDto> filterNotices(
			@RequestParam(required = false) String postedBy,
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer uploadedYear,
			@RequestParam(required = false) String department) {

		return noticeService.filterNotices(postedBy, year, uploadedYear, department);
	}

}
