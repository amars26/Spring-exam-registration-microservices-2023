package com.amars.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectDTO {

    private String studentCode;

    private String subject;

    private int score;

    private int grade;

    private String date;

    private boolean complete;

}
