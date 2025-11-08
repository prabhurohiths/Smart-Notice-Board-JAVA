package com.example.SmartNoticeBoard.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
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

    // ✅ Map Entity → DTO
    private NoticeDto mapToDto(Notice notice) {
        NoticeDto dto = new NoticeDto();
        dto.setId(notice.getId());
        dto.setTitle(notice.getTitle());
        dto.setDescription(notice.getDescription());
        dto.setDepartment(notice.getDepartment());
        dto.setYear(notice.getYear());
        dto.setPostedBy(notice.getPostedBy() != null ? notice.getPostedBy().getUsername() : null);
        dto.setPostedDate(notice.getPostedDate() != null ? notice.getPostedDate().toString() : null);
        dto.setModifiedBy(notice.getModifiedBy() != null ? notice.getModifiedBy().getUsername() : null);
        if (notice.getModifiedDate() != null)
            dto.setModifiedDate(notice.getModifiedDate().toString());

        // Convert image file names to Base64
//        if (notice.getImagePaths() != null && !notice.getImagePaths().isEmpty()) {
//            List<String> base64Images = Arrays.stream(notice.getImagePaths().split(","))
//                    .map(String::trim)
//                    .map(fileName -> {
//                        try {
//                            String fullPath = baseDir + fileName;
//                            byte[] fileContent = Files.readAllBytes(Paths.get(fullPath));
//
//                            String contentType = "image/jpeg";
//                            String lower = fileName.toLowerCase();
//                            if (lower.endsWith(".png")) contentType = "image/png";
//                            else if (lower.endsWith(".gif")) contentType = "image/gif";
//
//                            return "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(fileContent);
//                        } catch (Exception e) {
//                            System.err.println("❌ Failed to encode image: " + fileName + " → " + e.getMessage());
//                            return null;
//                        }
//                    })
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//            dto.setImagePaths(base64Images);
//        }
        
        if (notice.getImagePaths() != null && !notice.getImagePaths().isEmpty()) {
            List<String> fileNames = Arrays.stream(notice.getImagePaths().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            dto.setImageFileNames(fileNames); // ✅ Store actual filenames

            List<String> base64Images = fileNames.stream()
                    .map(fileName -> {
                        try {
                            String fullPath = baseDir + fileName;
                            byte[] fileContent = Files.readAllBytes(Paths.get(fullPath));
                            String contentType = "image/jpeg";
                            if (fileName.toLowerCase().endsWith(".png")) contentType = "image/png";
                            else if (fileName.toLowerCase().endsWith(".gif")) contentType = "image/gif";

                            return "data:" + contentType + ";base64," +
                                    Base64.getEncoder().encodeToString(fileContent);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            dto.setImagePaths(base64Images); // ✅ still send for preview
        }

        return dto;
    }

    // ✅ Map DTO → Entity
    private Notice mapToEntity(NoticeDto dto) {
        Notice notice = new Notice();
        notice.setId(dto.getId());
        notice.setTitle(dto.getTitle());
        notice.setDescription(dto.getDescription());
        notice.setDepartment(dto.getDepartment());
        notice.setYear(dto.getYear());
        notice.setPostedDate(dto.getPostedDate() != null
                ? LocalDateTime.parse(dto.getPostedDate())
                : LocalDateTime.now());
        if (dto.getModifiedDate() != null) {
            notice.setModifiedDate(LocalDateTime.parse(dto.getModifiedDate()));
        } else {
            notice.setModifiedDate(null);
        }

        return notice;
    }

    // ✅ Save multiple uploaded files
    private List<String> saveFiles(List<MultipartFile> files) {
        List<String> fileNames = new ArrayList<>();
        File dir = new File(baseDir);
        if (!dir.exists()) dir.mkdirs();

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

    // ✅ Create Notice
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

    // ✅ Update Notice with Image Replacement
    @Override
    public NoticeDto updateNoticeWithImages(Long id, NoticeDto noticeDto, List<MultipartFile> files) {
        Notice existing = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        existing.setTitle(noticeDto.getTitle());
        existing.setDescription(noticeDto.getDescription());
        existing.setDepartment(noticeDto.getDepartment());
        existing.setYear(noticeDto.getYear());
        existing.setModifiedDate(LocalDateTime.now());
        
        User user = userRepository.findByUsername(noticeDto.getModifiedBy());
        if (user == null) {
            throw new RuntimeException("User not found for username: " + noticeDto.getModifiedBy());
        }
        existing.setModifiedBy(user);

           
        // 🧩 Collect existing stored image filenames
        List<String> existingFiles = new ArrayList<>();
        if (existing.getImagePaths() != null && !existing.getImagePaths().isEmpty()) {
            existingFiles = Arrays.stream(existing.getImagePaths().split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        // 🧩 Collect remaining image base64 strings from frontend (if any)
        List<String> remainingFromFrontend = noticeDto.getImagePaths() != null
                ? noticeDto.getImagePaths()
                : new ArrayList<>();

        // 🧩 Delete removed old files (those present before but not sent now)
        for (String oldFile : existingFiles) {
            boolean stillExists = remainingFromFrontend.stream()
                    .anyMatch(base64 -> base64.contains(oldFile));
            if (!stillExists) {
                try {
                    Files.deleteIfExists(Paths.get(baseDir + oldFile));
                    System.out.println("🗑 Deleted old image: " + oldFile);
                } catch (Exception e) {
                    System.err.println("⚠️ Could not delete old image: " + oldFile);
                }
            }
        }

        // 🧩 Handle new uploaded files
        List<String> finalFileNames = existingFiles.stream()
                .filter(oldFile -> remainingFromFrontend.stream()
                        .anyMatch(base64 -> base64.contains(oldFile)))
                .collect(Collectors.toList());

        if (files != null && !files.isEmpty()) {
            List<String> newFiles = saveFiles(files);
            finalFileNames.addAll(newFiles);
        }

        // 🧩 Update DB with final file list
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
    public List<NoticeDto> getAllNotices() {
        return noticeRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
            // Delete associated images
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

    @Override
    public List<NoticeDto> filterNoticesByUserAndYear(String postedBy, Integer year, Integer uploadedYear, String department) {
        return noticeRepository.findAll().stream()
                .filter(n -> postedBy == null || (n.getPostedBy() != null &&
                        n.getPostedBy().getUsername().equalsIgnoreCase(postedBy)))
                .filter(n -> year == null || (n.getYear() != null && n.getYear().equals(year)))
                .filter(n -> uploadedYear == null || (n.getPostedDate() != null &&
                        n.getPostedDate().getYear() == uploadedYear))
                .filter(n -> department == null ||
                        department.equalsIgnoreCase("ALL") ||
                        (n.getDepartment() != null && n.getDepartment().equalsIgnoreCase(department)))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NoticeDto> getNoticesForStudent(String department, Integer year) {
        return noticeRepository.findAll().stream()
                .filter(n -> n.getDepartment() == null ||
                        n.getDepartment().equalsIgnoreCase("ALL") ||
                        n.getDepartment().equalsIgnoreCase(department))
                .filter(n -> n.getYear() == null ||
                        n.getYear() == 0 ||
                        n.getYear().equals(year))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
