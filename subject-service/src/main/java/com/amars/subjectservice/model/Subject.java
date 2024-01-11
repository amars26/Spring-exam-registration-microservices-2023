package com.amars.subjectservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String professor;

    private String professorCode;

    private String description;

    private String major;

}
