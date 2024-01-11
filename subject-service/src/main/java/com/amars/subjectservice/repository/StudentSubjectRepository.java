package com.amars.subjectservice.repository;

import com.amars.subjectservice.model.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    @Query("SELECT s FROM StudentSubject s WHERE s.studentCode  = :studentCode AND s.subject = :subject")
    StudentSubject findByStudentAndSubject(String studentCode , String subject);

    @Query("SELECT s FROM StudentSubject s WHERE s.studentCode  = :studentCode  AND s.complete = true")
    List<StudentSubject> findAllComplete(String studentCode );

    @Query("SELECT s FROM StudentSubject s WHERE s.studentCode  = :studentCode  AND s.complete = false")
    List<StudentSubject> findAllIncomplete(String studentCode);

}
