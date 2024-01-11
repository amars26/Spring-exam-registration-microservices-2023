package com.amars.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectSpecialDTO {

    private Long id;
    private String name;

    private String professor;

    private String professorCode;

    private String description;

    private String major;

}
