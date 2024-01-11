package com.amars.examservice.repository;

import com.amars.examservice.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    @Query("SELECT e FROM Exam e WHERE e.subject  = :subject")
    List<Exam> findAllBySubject(String subject);

}
