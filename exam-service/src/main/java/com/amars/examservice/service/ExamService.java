package com.amars.examservice.service;

import com.amars.examservice.dto.*;
import com.amars.examservice.model.Exam;
import com.amars.examservice.model.StudentExam;
import com.amars.examservice.repository.ExamRepository;
import com.amars.examservice.repository.StudentExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;

    private final StudentExamRepository studentExamRepository;

    public void createExam(ExamCreateDTO examCreateDTO){
        Exam examToSave = Exam.builder()
                .subject(examCreateDTO.getSubject())
                .date(examCreateDTO.getDate())
                .build();

        examRepository.save(examToSave);
    }

    public void updateExam(ExamUpdateDTO examUpdateDTO){
        Optional<Exam> optionalExam= examRepository.findById(examUpdateDTO.getId());
        Exam examToSave = optionalExam.get();
        examToSave.setDate(examUpdateDTO.getDate());
        examRepository.save(examToSave);
    }


    public void registerExam(ExamRegisterDTO examRegisterDTO){
        StudentExam studentExamToSave = StudentExam.builder()
                .idExam(examRegisterDTO.getIdExam())
                .studentCode(examRegisterDTO.getStudentCode())
                .score(0)
                .grade(0)
                .complete(false)
                .build();

        studentExamRepository.save(studentExamToSave);
    }

    public void gradeExam(ExamGradeDTO examGradeDTO){
        Optional<StudentExam> studentExamOptional = studentExamRepository.findById(examGradeDTO.getId());
        StudentExam studentExamToSave = studentExamOptional.get();
        studentExamToSave.setScore(examGradeDTO.getScore());
        studentExamToSave.setGrade(examGradeDTO.getGrade());
        studentExamToSave.setComplete(true);
        studentExamRepository.save(studentExamToSave);
    }

    public List<ExamResponseDTO> showExams(ExamRequestDTO examRequestDTO){
        List<Exam> exams = examRepository.findAllBySubject(examRequestDTO.getSubject());
        return exams.stream().map(this::mapToExamResponseDTO).toList();
    }

    private ExamResponseDTO mapToExamResponseDTO(Exam examToConvert) {
        return ExamResponseDTO.builder()
                .id(examToConvert.getId())
                .subject(examToConvert.getSubject())
                .date(examToConvert.getDate())
                .build();
    }

    public List<ExamStudentsResponseDTO> showStudentsExam(ExamStudentsRequestDTO examStudentsRequestDTO){
        List<StudentExam> studentScores = studentExamRepository.findAllByExamId(examStudentsRequestDTO.getId());
        return studentScores.stream().map(this::mapToExamStudentsResponseDTO).toList();
    }

    private ExamStudentsResponseDTO mapToExamStudentsResponseDTO(StudentExam studentExamToConvert) {
        return ExamStudentsResponseDTO.builder()
                .studentCode(studentExamToConvert.getStudentCode())
                .score(studentExamToConvert.getScore())
                .grade(studentExamToConvert.getGrade())
                .id(studentExamToConvert.getId())
                .complete(studentExamToConvert.isComplete())
                .build();
    }


    public List<ExamIdsDTO> showStudentsExam(ExamRequestDTO examRequestDTO){
        List<StudentExam> studentScores = studentExamRepository.findAllByStudentCode(examRequestDTO.getSubject());
        return studentScores.stream().map(this::mapToExamIdsDTO).toList();
    }

    private ExamIdsDTO mapToExamIdsDTO(StudentExam studentExamToConvert) {
        return ExamIdsDTO.builder()
                .idExam(studentExamToConvert.getIdExam().intValue())
                .build();
    }

    public List<ExamStudentsResponseDTO> showExamsForStudent(AllExamRequestDTO allExamRequestDTO){
        List<StudentExam> studentExams = studentExamRepository.findExamsByStudentSubject(allExamRequestDTO.getStudentCode());
        return studentExams.stream().map(this::mapToExamStudentsResponseDTOv2).toList();
    }

    public List<ExamResponseDTO> showAllExams(){
        List<Exam> exams = examRepository.findAll();
        return exams.stream().map(this::mapToExamResponseDTO).toList();
    }

    private ExamStudentsResponseDTO mapToExamStudentsResponseDTOv2(StudentExam studentExamToConvert) {
        return ExamStudentsResponseDTO.builder()
                .studentCode(studentExamToConvert.getStudentCode())
                .score(studentExamToConvert.getScore())
                .grade(studentExamToConvert.getGrade())
                .id(studentExamToConvert.getIdExam())
                .complete(studentExamToConvert.isComplete())
                .build();
    }

}
