package com.example.SmartNoticeBoard.service;

import java.util.List;

import com.example.SmartNoticeBoard.DTO.YearDto;

public interface YearService {
	List<YearDto> getAllYears();

	YearDto addYear(YearDto yearLevelDto);
}
