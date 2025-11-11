package com.example.SmartNoticeBoard.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SmartNoticeBoard.DTO.YearDto;
import com.example.SmartNoticeBoard.model.Year;
import com.example.SmartNoticeBoard.repository.YearRepository;

@Service
public class YearServiceImpl implements YearService {

	@Autowired
	private YearRepository yearRepo;

	private YearDto mapToDto(Year year) {
		return new YearDto(year.getId(), year.getYearNumber(), year.getYearName());
	}

	private Year mapToEntity(YearDto dto) {
		Year year = new Year();
		year.setId(dto.getId());
		year.setYearNumber(dto.getYearNumber());
		year.setYearName(dto.getYearName());
		return year;
	}

	@Override
	public List<YearDto> getAllYears() {
		return yearRepo.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Override
	public YearDto addYear(YearDto dto) {
		if (yearRepo.findByYearNumber(dto.getYearNumber()) != null) {
			throw new RuntimeException("Year already exists: " + dto.getYearNumber());
		}
		Year saved = yearRepo.save(mapToEntity(dto));
		return mapToDto(saved);
	}
}
