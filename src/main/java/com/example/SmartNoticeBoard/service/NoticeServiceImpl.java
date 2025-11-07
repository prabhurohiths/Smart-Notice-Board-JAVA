package com.example.SmartNoticeBoard.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	@Value("${notice.image.base-path}")
    private String baseDir;
    

	private NoticeDto mapToDto(Notice notice) {
	    NoticeDto dto = new NoticeDto();
	    dto.setId(notice.getId());
	    dto.setTitle(notice.getTitle());
	    dto.setDescription(notice.getDescription());
	    dto.setDepartment(notice.getDepartment());
	    dto.setYear(notice.getYear());
	    dto.setPostedBy(notice.getPostedBy() != null ? notice.getPostedBy().getUsername() : null);

	    if (notice.getPostedDate() != null) {
	        dto.setPostedDate(notice.getPostedDate().toString());
	    }


	    // ✅ Convert image file names to Base64 strings
	    if (notice.getImagePaths() != null && !notice.getImagePaths().isEmpty()) {
	        List<String> base64Images = Arrays.stream(notice.getImagePaths().split(","))
	                .map(String::trim)
	                .map(fileName -> {
	                    try {
	                        String fullPath = baseDir + fileName; // ✅ Rebuild full path
	                        byte[] fileContent = Files.readAllBytes(Paths.get(fullPath));

	                        // ✅ Detect MIME type by extension
	                        String contentType = "image/jpeg";
	                        String lower = fileName.toLowerCase();
	                        if (lower.endsWith(".png")) contentType = "image/png";
	                        else if (lower.endsWith(".gif")) contentType = "image/gif";

	                        // ✅ Return Base64 data URL
	                        return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(fileContent);
	                    } catch (Exception e) {
	                        System.err.println("❌ Failed to encode image: " + fileName + " → " + e.getMessage());
	                        return null;
	                    }
	                })
	                .filter(Objects::nonNull)
	                .collect(Collectors.toList());

	        dto.setImagePaths(base64Images);
	    }

	    return dto;
	}



	private Notice mapToEntity(NoticeDto dto) {
		Notice notice = new Notice();
		notice.setId(dto.getId());
		notice.setTitle(dto.getTitle());
		notice.setDescription(dto.getDescription());
		notice.setDepartment(dto.getDepartment());
		notice.setYear(dto.getYear());
	    if (dto.getPostedDate() != null) {
	        notice.setPostedDate(LocalDateTime.parse(dto.getPostedDate()));
	    } else {
	        notice.setPostedDate(LocalDateTime.now());
	    }
	    return notice;
	}

//	@Override
//	public NoticeDto createNotice(NoticeDto noticeDto, Long postedById) {
//		User user = userRepository.findById(postedById).orElseThrow(() -> new RuntimeException("User not found"));
//
//		Notice notice = mapToEntity(noticeDto);
//		notice.setPostedBy(user);
//		Notice saved = noticeRepository.save(notice);
//		return mapToDto(saved);
//	}

	
	@Override
	public NoticeDto createNotice(NoticeDto noticeDto, Long postedById, List<MultipartFile> images) {
	    User user = userRepository.findById(postedById)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Notice notice = mapToEntity(noticeDto);
	    notice.setPostedBy(user);

	    // Save multiple images
	    if (images != null && !images.isEmpty()) {
	        List<String> imageNames = new ArrayList<>(); // ✅ store only names
//	      String baseDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "notices" + File.separator;

	        File dir = new File(baseDir);
	        if (!dir.exists()) dir.mkdirs();

	        for (MultipartFile image : images) {
	            try {
	                // ✅ Create a unique filename
	                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

	                // ✅ Create full save path
	                String filePath = baseDir + fileName;

	                // Save file to disk
	                image.transferTo(new File(filePath));

	                // ✅ Add only the file name to DB list
	                imageNames.add(fileName);

	            } catch (Exception e) {
	                throw new RuntimeException("Error saving image: " + image.getOriginalFilename(), e);
	            }
	        }

	        // ✅ Save only file names (comma-separated) in DB
	        notice.setImagePaths(String.join(",", imageNames));
	    }

	    Notice saved = noticeRepository.save(notice);
	    return mapToDto(saved);
	}


	@Override
	public NoticeDto updateNotice(Long id, NoticeDto noticeDto) {
		Notice existing = noticeRepository.findById(id).orElseThrow(() -> new RuntimeException("Notice not found"));

		existing.setTitle(noticeDto.getTitle());
		existing.setDescription(noticeDto.getDescription());
		existing.setDepartment(noticeDto.getDepartment());
		existing.setYear(noticeDto.getYear());
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
	public List<NoticeDto> getNoticesForStudent(String department, Integer year) {
	    List<Notice> allNotices = noticeRepository.findAll();

	    return allNotices.stream()
	        .filter(n ->
	            n.getDepartment() == null ||
	            n.getDepartment().equalsIgnoreCase("ALL") ||
	            n.getDepartment().equalsIgnoreCase(department)
	        )
	        .filter(n ->
	            n.getYear() == null ||
	            n.getYear() == 0 ||
	            n.getYear().equals(year)
	        )
	        .map(this::mapToDto)
	        .collect(Collectors.toList());
	}

	
	@Override
	public void deleteNoticeWithRoleCheck(Long id, Long userId) {
	    Notice notice = noticeRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Notice not found"));

	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
	    boolean isTeacher = user.getRoles().stream().anyMatch(r -> r.getName().equals("TEACHER"));

	    if (isAdmin) {
	        noticeRepository.delete(notice);
	    } else if (isTeacher) {
	        // Teachers can delete only their own notices
	        if (notice.getPostedBy().getId().equals(userId)) {
	            noticeRepository.delete(notice);
	        } else {
	            throw new RuntimeException("You can only delete your own notices.");
	        }
	    } else {
	        throw new RuntimeException("Students cannot delete notices.");
	    }
	}
	
	
	@Override
	public List<NoticeDto> filterNoticesByUserAndYear(String postedBy, Integer year, Integer uploadedYear, String department) {
	    List<Notice> allNotices = noticeRepository.findAll();

	    return allNotices.stream()
	        // 🔹 Filter by postedBy (admin/teacher)
	        .filter(n -> postedBy == null ||
	            (n.getPostedBy() != null &&
	             n.getPostedBy().getUsername() != null &&
	             n.getPostedBy().getUsername().equalsIgnoreCase(postedBy)))

	        // 🔹 Filter by academic year (1st, 2nd, etc.)
	        .filter(n -> year == null || (n.getYear() != null && n.getYear().equals(year)))

	        // 🔹 Filter by uploaded year (e.g., 2025)
	        .filter(n -> {
	            if (uploadedYear == null || n.getPostedDate() == null) return true;
	            return n.getPostedDate().getYear() == uploadedYear;
	        })

	        // 🔹 Filter by department (handles "ALL")
	        .filter(n -> department == null ||
	            department.equalsIgnoreCase("ALL") ||
	            (n.getDepartment() != null && n.getDepartment().equalsIgnoreCase(department)))

	        .map(this::mapToDto)
	        .collect(Collectors.toList());
	}


}
