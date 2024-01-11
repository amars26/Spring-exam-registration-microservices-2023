package com.amars.examservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamGradeDTO {

    private Long id;

    private int score;

    private int grade;

    private boolean complete;

}
