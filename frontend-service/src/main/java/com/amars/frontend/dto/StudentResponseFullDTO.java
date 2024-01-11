package com.amars.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseFullDTO {

    private String studentCode;

    private String firstName;

    private String lastName;

    private String identificationNumber;

    private String birthDate;

    private int year;

    private String department;

    private String major;

    private String status;

}
