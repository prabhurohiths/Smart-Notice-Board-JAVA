package com.example.SmartNoticeBoard.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SmartNoticeBoard.DTO.NoticeDto;
import com.example.SmartNoticeBoard.model.Notice;
import com.example.SmartNoticeBoard.model.User;
import com.example.SmartNoticeBoard.repository.NoticeRepository;
import com.example.SmartNoticeBoard.repository.UserRepository;

@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	private NoticeRepository noticeRepository;

	@Autowired
	private UserRepository userRepository;

	private NoticeDto mapToDto(Notice notice) {
		NoticeDto dto = new NoticeDto();
		dto.setId(notice.getId());
		dto.setTitle(notice.getTitle());
		dto.setDescription(notice.getDescription());
		dto.setDepartment(notice.getDepartment());
		dto.setBranch(notice.getBranch());
		dto.setYear(notice.getYear());
		dto.setSection(notice.getSection());
		dto.setPostedBy(notice.getPostedBy() != null ? notice.getPostedBy().getUsername() : null);
		if (notice.getPostedDate() != null) {
	        dto.setPostedDate(notice.getPostedDate().toString()); // ✅ Convert LocalDateTime → String
	    }
		return dto;
	}

	private Notice mapToEntity(NoticeDto dto) {
		Notice notice = new Notice();
		notice.setId(dto.getId());
		notice.setTitle(dto.getTitle());
		notice.setDescription(dto.getDescription());
		notice.setDepartment(dto.getDepartment());
		notice.setBranch(dto.getBranch());
		notice.setYear(dto.getYear());
		notice.setSection(dto.getSection());
	    if (dto.getPostedDate() != null) {
	        notice.setPostedDate(LocalDateTime.parse(dto.getPostedDate()));
	    } else {
	        notice.setPostedDate(LocalDateTime.now());
	    }
	    return notice;
	}

	@Override
	public NoticeDto createNotice(NoticeDto noticeDto, Long postedById) {
		User user = userRepository.findById(postedById).orElseThrow(() -> new RuntimeException("User not found"));

		Notice notice = mapToEntity(noticeDto);
		notice.setPostedBy(user);
		Notice saved = noticeRepository.save(notice);
		return mapToDto(saved);
	}

	@Override
	public NoticeDto updateNotice(Long id, NoticeDto noticeDto) {
		Notice existing = noticeRepository.findById(id).orElseThrow(() -> new RuntimeException("Notice not found"));

		existing.setTitle(noticeDto.getTitle());
		existing.setDescription(noticeDto.getDescription());
		existing.setDepartment(noticeDto.getDepartment());
		existing.setBranch(noticeDto.getBranch());
		existing.setYear(noticeDto.getYear());
		existing.setSection(noticeDto.getSection());
		existing.setPostedDate(LocalDateTime.now());

		Notice updated = noticeRepository.save(existing);
		return mapToDto(updated);
	}

	@Override
	public void deleteNotice(Long id) {
		noticeRepository.deleteById(id);
	}

	@Override
	public List<NoticeDto> getAllNotices() {
		return noticeRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Override
	public List<NoticeDto> getNoticesForStudent(String department, String branch, Integer year, String section) {
		List<Notice> allNotices = noticeRepository.findAll();
		return allNotices.stream().filter(n -> (n.getDepartment() == null || n.getDepartment().equals(department)))
				.filter(n -> (n.getBranch() == null || n.getBranch().equals(branch)))
				.filter(n -> (n.getYear() == null || n.getYear().equals(year)))
				.filter(n -> (n.getSection() == null || n.getSection().equals(section))).map(this::mapToDto)
				.collect(Collectors.toList());
	}
}
