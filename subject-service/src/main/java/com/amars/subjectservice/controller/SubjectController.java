package com.amars.subjectservice.controller;

import com.amars.subjectservice.dto.*;
import com.amars.subjectservice.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("/createSubject")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSubject(@RequestBody SubjectDTO subjectDTO){
        subjectService.createOrUpdateSubject(subjectDTO);
    }

    @GetMapping("/subjects")
    @ResponseStatus(HttpStatus.OK)
    public List<SubjectSpecialDTO> getAllSubjects(){
        return subjectService.getAllSubjects();
    }

    @GetMapping("/getAllStudentSubjects")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentSubjectSpecialDTO> getAllStudentSubjects(){
        return subjectService.getAllStudentsOnSubjects();
    }

    @PostMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public SubjectDTO getSubject(@RequestBody SubjectRequestDTO subjectRequestDTO){
        return subjectService.findSubjectByName(subjectRequestDTO);
    }

    @PostMapping("/addStudentToSubject")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudentSubject(@RequestBody StudentSubjectDTO studentSubjectDTO){
        subjectService.createOrUpdateStudentSubject(studentSubjectDTO);
    }

    @PostMapping("/showCompleteSubjects")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentSubjectDTO> getCompleteSubjects(@RequestBody StudentSubjectRequestDTO studentSubjectRequestDTO){
        return subjectService.showCompletedSubjectsForStudent(studentSubjectRequestDTO);
    }

    @PostMapping("/showIncompleteSubjects")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentSubjectDTO> getIncompleteSubjects(@RequestBody StudentSubjectRequestDTO studentSubjectRequestDTO){
        return subjectService.showOngoingSubjectsForStudent(studentSubjectRequestDTO);
    }



}
