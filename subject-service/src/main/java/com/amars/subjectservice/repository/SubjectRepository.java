package com.amars.subjectservice.repository;

import com.amars.subjectservice.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("SELECT s FROM Subject s WHERE s.name = :subjectName")
    Subject findByName(String subjectName);

}
