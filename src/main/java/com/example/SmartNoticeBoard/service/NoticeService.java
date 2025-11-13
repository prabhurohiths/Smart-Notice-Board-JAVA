package com.example.SmartNoticeBoard.service;


import com.example.SmartNoticeBoard.DTO.NoticeDto;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {
    NoticeDto createNotice(NoticeDto noticeDto, Long postedById, List<MultipartFile> images);

    void deleteNotice(Long id);
    
    NoticeDto getNoticeById(Long id);
    
    List<NoticeDto> getAllNoticess();

    Map<String, Object> getAllNotices(int page, int size);
    
    Map<String, Object> getNoticesForStudent(String department, Integer year, int page, int size);

    Map<String, Object> filterNotices(String postedBy, Integer year, Integer uploadedYear, String department, int page, int size);
    
    Map<String, Object> studentFilterNotices(String postedBy, Integer uploadedYear, String department, Integer year, int page, int size);

    NoticeDto updateNoticeWithImages(Long id, NoticeDto noticeDto, List<MultipartFile> files);
    
    void deleteNoticeWithRoleCheck(Long id, Long userId);

}
