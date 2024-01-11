package com.amars.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private String firstName;

    private String lastName;

    private String birthDate;

    private int year;

    private String department;

    private String major;

    private String status;

}
