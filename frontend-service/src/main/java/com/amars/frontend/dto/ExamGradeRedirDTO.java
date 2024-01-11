package com.amars.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamGradeRedirDTO {

    private Long id;

    private int score;

    private int grade;

    private boolean complete;

    private String redirect;

}
