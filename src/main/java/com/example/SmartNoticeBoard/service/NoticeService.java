package com.example.SmartNoticeBoard.service;


import com.example.SmartNoticeBoard.DTO.NoticeDto;
import java.util.List;

public interface NoticeService {
    NoticeDto createNotice(NoticeDto noticeDto, Long postedById);

    NoticeDto updateNotice(Long id, NoticeDto noticeDto);

    void deleteNotice(Long id);

    List<NoticeDto> getAllNotices();

    List<NoticeDto> getNoticesForStudent(String department, String branch, Integer year, String section);
}
