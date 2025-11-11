package com.example.SmartNoticeBoard.service;


import com.example.SmartNoticeBoard.DTO.NoticeDto;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {
    NoticeDto createNotice(NoticeDto noticeDto, Long postedById, List<MultipartFile> images);

    void deleteNotice(Long id);
    
    NoticeDto getNoticeById(Long id);

    List<NoticeDto> getAllNotices();
    
    NoticeDto updateNoticeWithImages(Long id, NoticeDto noticeDto, List<MultipartFile> files);

    List<NoticeDto> getNoticesForStudent(String department, Integer year);
    
    void deleteNoticeWithRoleCheck(Long id, Long userId);

    List<NoticeDto> filterNotices(String postedBy, Integer year, Integer uploadedYear, String department);

}
