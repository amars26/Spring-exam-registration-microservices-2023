package com.amars.examservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="studentexam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExam {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long idExam;

    private String studentCode;

    private int score;

    private int grade;

    private boolean complete;

}
