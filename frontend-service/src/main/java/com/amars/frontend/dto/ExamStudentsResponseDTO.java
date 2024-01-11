package com.amars.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamStudentsResponseDTO {

    private Long id;

    private boolean complete;

    private String studentCode;

    private int score;

    private int grade;

}
