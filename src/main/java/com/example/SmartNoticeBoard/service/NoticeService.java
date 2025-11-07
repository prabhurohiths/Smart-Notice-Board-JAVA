package com.example.SmartNoticeBoard.service;


import com.example.SmartNoticeBoard.DTO.NoticeDto;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {
    NoticeDto createNotice(NoticeDto noticeDto, Long postedById, List<MultipartFile> images);

    NoticeDto updateNotice(Long id, NoticeDto noticeDto);

    void deleteNotice(Long id);

    List<NoticeDto> getAllNotices();

    List<NoticeDto> getNoticesForStudent(String department, Integer year);
    
    void deleteNoticeWithRoleCheck(Long id, Long userId);

    List<NoticeDto> filterNoticesByUserAndYear(String postedBy, Integer year, Integer uploadedYear, String department);

}
