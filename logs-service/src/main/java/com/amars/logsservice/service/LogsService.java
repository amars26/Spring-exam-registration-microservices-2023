package com.amars.logsservice.service;

import com.amars.logsservice.dto.LogsDTO;
import com.amars.logsservice.model.Logs;
import com.amars.logsservice.repository.LogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogsService {

    private final LogsRepository logsRepository;

    public void createLog(LogsDTO logsDTO){
        Logs logToSave = Logs.builder()
                .called_service(logsDTO.getCalled_service())
                .called_function(logsDTO.getCalled_function())
                .log_date(logsDTO.getLog_date())
                .build();
        logsRepository.save(logToSave);
    }


    public List<LogsDTO> getAllLogs() {
        List<Logs> logs = logsRepository.findAll();
        return logs.stream().map(this::mapToLogsDTO).toList();

    }

    private LogsDTO mapToLogsDTO(Logs logsToConvert) {
        return LogsDTO.builder()
                .called_service(logsToConvert.getCalled_service())
                .called_function(logsToConvert.getCalled_function())
                .log_date(logsToConvert.getLog_date())
                .build();
    }


}
