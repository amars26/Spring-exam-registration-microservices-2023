package com.amars.examservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String subject;

    private String date;



}
