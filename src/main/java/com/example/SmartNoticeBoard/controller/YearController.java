package com.example.SmartNoticeBoard.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.SmartNoticeBoard.DTO.YearDto;
import com.example.SmartNoticeBoard.service.YearService;

@RestController
@RequestMapping("/api/year")
public class YearController {

    @Autowired
    private YearService yearService;

    @GetMapping("/getAllYears")
    public List<YearDto> getAllYears() {
        return yearService.getAllYears();
    }

    @PostMapping("/add")
    public ResponseEntity<YearDto> addYear(@RequestBody YearDto YearDto) {
        YearDto saved = yearService.addYear(YearDto);
        return ResponseEntity.ok(saved);
    }
}
