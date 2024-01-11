package com.amars.logsservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value= "logs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Logs {

    @Id
    private String id;

    private String called_service;

    private String called_function;

    private String log_date;


}
