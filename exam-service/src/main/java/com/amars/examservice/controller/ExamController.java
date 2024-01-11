package com.amars.examservice.controller;

import com.amars.examservice.dto.*;
import com.amars.examservice.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/createExam")
    @ResponseStatus(HttpStatus.CREATED)
    public void createExam(@RequestBody ExamCreateDTO examCreateDTO){
        examService.createExam(examCreateDTO);
    }

    @PostMapping("/updateExam")
    @ResponseStatus(HttpStatus.OK)
    public void updateExam(@RequestBody ExamUpdateDTO examUpdateDTO){
        examService.updateExam(examUpdateDTO);
    }

    @PostMapping("/exams")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamResponseDTO> showExams(@RequestBody ExamRequestDTO examRequestDTO){
        return examService.showExams(examRequestDTO);
    }

    @PostMapping("/studentsExam")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamStudentsResponseDTO> showExams(@RequestBody ExamStudentsRequestDTO examStudentsRequestDTO){
        return examService.showStudentsExam(examStudentsRequestDTO);
    }

    @PostMapping("/registerExam")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerExam(@RequestBody ExamRegisterDTO examRegisterDTO){
        examService.registerExam(examRegisterDTO);
    }

    @PostMapping("/gradeExam")
    @ResponseStatus(HttpStatus.OK)
    public void gradeExam(@RequestBody ExamGradeDTO examGradeDTO){
        examService.gradeExam(examGradeDTO);
    }

    @PostMapping("/getRegisteredExams")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamIdsDTO> showRegisteredExams(@RequestBody ExamRequestDTO examRequestDTO){
        return examService.showStudentsExam(examRequestDTO);
    }

    @PostMapping("/examsForStudent")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamStudentsResponseDTO> showExamsForStudent(@RequestBody AllExamRequestDTO allExamRequestDTO){
        return examService.showExamsForStudent(allExamRequestDTO);
    }

    @GetMapping("/examsAll")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamResponseDTO> showAllExams(){
        return examService.showAllExams();
    }

}
