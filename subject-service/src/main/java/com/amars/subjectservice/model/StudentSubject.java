package com.amars.subjectservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="studentsubject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String studentCode;

    private String subject;

    private int score;

    private int grade;

    private String date;

    private boolean complete;


}
