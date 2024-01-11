package com.amars.logsservice.controller;

import com.amars.logsservice.dto.LogsDTO;
import com.amars.logsservice.service.LogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogsController {

    private final LogsService logsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createLog(@RequestBody LogsDTO logsDTO){
        logsService.createLog(logsDTO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LogsDTO> getAllLogs(){
        return logsService.getAllLogs();
    }

}
