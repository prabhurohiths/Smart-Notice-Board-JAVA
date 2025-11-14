package com.example.SmartNoticeBoard.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.SmartNoticeBoard.DTO.NoticeDto;
import com.example.SmartNoticeBoard.model.Department;
import com.example.SmartNoticeBoard.model.Notice;
import com.example.SmartNoticeBoard.model.User;
import com.example.SmartNoticeBoard.model.Year;
import com.example.SmartNoticeBoard.repository.DepartmentRepository;
import com.example.SmartNoticeBoard.repository.NoticeRepository;
import com.example.SmartNoticeBoard.repository.UserRepository;
import com.example.SmartNoticeBoard.repository.YearRepository;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private YearRepository YearRepository;

    @Value("${notice.image.base-path}")
    private String baseDir;

    // Map Entity → DTO
    private NoticeDto mapToDto(Notice notice) {
        NoticeDto dto = new NoticeDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setDescription(notice.getDescription());
        dto.setDepartment(notice.getDepartment() != null ? notice.getDepartment().getName() : null);
        dto.setYear(notice.getYear() != null ? notice.getYear().getYearNumber() : null);
        dto.setPostedBy(notice.getPostedBy() != null ? notice.getPostedBy().getUsername() : null);
        dto.setPostedDate(notice.getPostedDate() != null ? notice.getPostedDate().toString() : null);
        dto.setModifiedBy(notice.getModifiedBy() != null ? notice.getModifiedBy().getUsername() : null);
        if (notice.getModifiedDate() != null)
            dto.setModifiedDate(notice.getModifiedDate().toString());

        // ✅ Convert image file names to Base64
        if (notice.getImagePaths() != null && !notice.getImagePaths().isEmpty()) {
            List<String> fileNames = Arrays.stream(notice.getImagePaths().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            dto.setImageFileNames(fileNames);

            List<String> base64Images = fileNames.stream().map(fileName -> {
                try {
                    String fullPath = baseDir + fileName;
                    byte[] fileContent = Files.readAllBytes(Paths.get(fullPath));
                    String contentType = "image/jpeg";
                    if (fileName.toLowerCase().endsWith(".png")) contentType = "image/png";
                    else if (fileName.toLowerCase().endsWith(".gif")) contentType = "image/gif";

                    return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(fileContent);
                } catch (Exception e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            dto.setImagePaths(base64Images);
        }

        return dto;
    }

    // Map DTO → Entity
    private Notice mapToEntity(NoticeDto dto) {
        Notice notice = new Notice();
        notice.setId(dto.getId());
        notice.setTitle(dto.getTitle());
        notice.setDescription(dto.getDescription());

        // Department validation — must exist, not auto-create
        if (dto.getDepartment() != null && !dto.getDepartment().isBlank()) {
            Department dept = departmentRepository.findByName(dto.getDepartment());
            if (dept == null) {
                throw new RuntimeException("Invalid department: " + dto.getDepartment());
            }
            notice.setDepartment(dept);
        }

        // Year mapping using Year
        if (dto.getYear() != null) {
            Year Year = YearRepository.findByYearNumber(dto.getYear());
            if (Year == null) {
                throw new RuntimeException("Invalid year: " + dto.getYear());
            }
            notice.setYear(Year);
        }

        notice.setPostedDate(
                dto.getPostedDate() != null ? LocalDateTime.parse(dto.getPostedDate()) : LocalDateTime.now());
        notice.setModifiedDate(dto.getModifiedDate() != null ? LocalDateTime.parse(dto.getModifiedDate()) : null);

        return notice;
    }

    // Save multiple uploaded files
    private List<String> saveFiles(List<MultipartFile> files) {
        List<String> fileNames = new ArrayList<>();
        File dir = new File(baseDir);
        if (!dir.exists())
            dir.mkdirs();

        for (MultipartFile file : files) {
            try {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String filePath = baseDir + fileName;
                file.transferTo(new File(filePath));
                fileNames.add(fileName);
            } catch (Exception e) {
                throw new RuntimeException("Error saving image: " + file.getOriginalFilename(), e);
            }
        }
        return fileNames;
    }

    // Create Notice
    @Override
    public NoticeDto createNotice(NoticeDto noticeDto, Long postedById, List<MultipartFile> images) {
        User user = userRepository.findById(postedById)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notice notice = mapToEntity(noticeDto);
        notice.setPostedBy(user);
        notice.setModifiedBy(user);

        if (images != null && !images.isEmpty()) {
            List<String> imageNames = saveFiles(images);
            notice.setImagePaths(String.join(",", imageNames));
        }

        Notice saved = noticeRepository.save(notice);
        return mapToDto(saved);
    }

    // Update Notice with Image Replacement
    @Override
    public NoticeDto updateNoticeWithImages(Long id, NoticeDto noticeDto, List<MultipartFile> files) {
        Notice existing = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        existing.setTitle(noticeDto.getTitle());
        existing.setDescription(noticeDto.getDescription());
        existing.setModifiedDate(LocalDateTime.now());

        // Department validation — must exist
        if (noticeDto.getDepartment() != null && !noticeDto.getDepartment().isBlank()) {
            Department dept = departmentRepository.findByName(noticeDto.getDepartment());
            if (dept == null) {
                throw new RuntimeException("Invalid department: " + noticeDto.getDepartment());
            }
            existing.setDepartment(dept);
        }

        // Year validation — must exist
        if (noticeDto.getYear() != null) {
            Year Year = YearRepository.findByYearNumber(noticeDto.getYear());
            if (Year == null) {
                throw new RuntimeException("Invalid year: " + noticeDto.getYear());
            }
            existing.setYear(Year);
        }

        // Modified By user
        User user = userRepository.findByUsername(noticeDto.getModifiedBy());
        if (user == null) {
            throw new RuntimeException("User not found for username: " + noticeDto.getModifiedBy());
        }
        existing.setModifiedBy(user);

        // Manage image file updates
        List<String> existingFiles = new ArrayList<>();
        if (existing.getImagePaths() != null && !existing.getImagePaths().isEmpty()) {
            existingFiles = Arrays.stream(existing.getImagePaths().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        List<String> remainingFromFrontend = noticeDto.getImagePaths() != null
                ? noticeDto.getImagePaths()
                : new ArrayList<>();

        for (String oldFile : existingFiles) {
            boolean stillExists = remainingFromFrontend.stream().anyMatch(base64 -> base64.contains(oldFile));
            if (!stillExists) {
                try {
                    Files.deleteIfExists(Paths.get(baseDir + oldFile));
                } catch (Exception e) {
                    System.err.println("⚠️ Could not delete old image: " + oldFile);
                }
            }
        }

        List<String> finalFileNames = existingFiles.stream()
                .filter(oldFile -> remainingFromFrontend.stream().anyMatch(base64 -> base64.contains(oldFile)))
                .collect(Collectors.toList());

        if (files != null && !files.isEmpty()) {
            List<String> newFiles = saveFiles(files);
            finalFileNames.addAll(newFiles);
        }

        existing.setImagePaths(finalFileNames.isEmpty() ? null : String.join(",", finalFileNames));

        Notice updated = noticeRepository.save(existing);
        return mapToDto(updated);
    }

    @Override
    public NoticeDto getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        return mapToDto(notice);
    }

    @Override
    public List<NoticeDto> getAllNoticess() {
        return noticeRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getAllNotices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedDate").descending());
        Page<Notice> noticePage = noticeRepository.findAll(pageable);

        List<NoticeDto> notices = noticePage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("currentPage", noticePage.getNumber());
        response.put("totalItems", noticePage.getTotalElements());
        response.put("totalPages", noticePage.getTotalPages());

        return response;
    }

    @Override
    public Map<String, Object> filterNotices(String postedBy, Integer year, Integer uploadedYear, String department, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedDate").descending());
        
        Page<Notice> noticePage = noticeRepository.filterNoticesFromDb(postedBy, year, uploadedYear, department, pageable);
        
        List<NoticeDto> notices = noticePage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("currentPage", noticePage.getNumber());
        response.put("totalItems", noticePage.getTotalElements());
        response.put("totalPages", noticePage.getTotalPages());
        
        return response;
    }

    @Override
    public Map<String, Object> studentFilterNotices(String postedBy, Integer uploadedYear, String department, Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedDate").descending());
        Page<Notice> noticePage = noticeRepository.studentFilterNotices(postedBy, uploadedYear, department, year, pageable);

        List<NoticeDto> notices = noticePage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("currentPage", noticePage.getNumber());
        response.put("totalItems", noticePage.getTotalElements());
        response.put("totalPages", noticePage.getTotalPages());
        return response;
    }





    @Override
    public Map<String, Object> getNoticesForStudent(String department, Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("postedDate").descending());

        // Fetch department = student's + ALL, year = student's + ALL
        Page<Notice> noticePage = noticeRepository.findStudentNotices(department, year, pageable);

        List<NoticeDto> notices = noticePage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("currentPage", noticePage.getNumber());
        response.put("totalItems", noticePage.getTotalElements());
        response.put("totalPages", noticePage.getTotalPages());
        return response;
    }


    @Override
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    @Override
    public void deleteNoticeWithRoleCheck(Long id, Long userId) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isTeacher = user.getRoles().stream().anyMatch(r -> r.getName().equals("TEACHER"));

        if (isAdmin || (isTeacher && notice.getPostedBy().getId().equals(userId))) {
            if (notice.getImagePaths() != null) {
                Arrays.stream(notice.getImagePaths().split(","))
                        .map(String::trim)
                        .forEach(fileName -> {
                            try {
                                Files.deleteIfExists(Paths.get(baseDir + fileName));
                            } catch (Exception e) {
                                System.err.println("⚠️ Failed to delete image: " + fileName);
                            }
                        });
            }
            noticeRepository.delete(notice);
        } else {
            throw new RuntimeException("You are not authorized to delete this notice.");
        }
    }
}
