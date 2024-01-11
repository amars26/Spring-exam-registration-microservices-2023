package org.amars.departmentservice.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="subjectdepartment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private Long departmentId;

    private Long year;


}
