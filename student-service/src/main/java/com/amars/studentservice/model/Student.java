package com.amars.studentservice.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="student")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
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
