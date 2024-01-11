package com.amars.examservice.repository;

import com.amars.examservice.model.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {

    @Query("SELECT s FROM StudentExam s WHERE s.idExam  = :idExam")
    List<StudentExam> findAllByExamId(Long idExam);

    @Query("SELECT s FROM StudentExam s WHERE s.studentCode  = :studentCode")
    List<StudentExam> findAllByStudentCode(String studentCode);

    @Query("SELECT s FROM StudentExam s WHERE s.studentCode  = :studentCode")
    List<StudentExam> findExamsByStudentSubject(String studentCode);

}
