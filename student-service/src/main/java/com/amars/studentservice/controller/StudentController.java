package com.amars.studentservice.controller;

import com.amars.studentservice.dto.StudentDTO;
import com.amars.studentservice.dto.StudentRequestDTO;
import com.amars.studentservice.dto.StudentResponseDTO;
import com.amars.studentservice.dto.StudentResponseFullDTO;
import com.amars.studentservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudent(@RequestBody StudentDTO studentDTO){
        studentService.createOrUpdateStudent(studentDTO);
    }

    @PostMapping("/studentinfo")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponseDTO studentinfo(@RequestBody StudentRequestDTO studentRequestDTO){
        return studentService.studentinfo(studentRequestDTO);
    }

    @PostMapping("/fullstudentinfo")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponseFullDTO fullstudentinfo(@RequestBody StudentRequestDTO studentRequestDTO){
        return studentService.fullStudentInfo(studentRequestDTO);
    }


    @PostMapping("/nextyear")
    @ResponseStatus(HttpStatus.OK)
    public void nextYear(@RequestBody StudentRequestDTO studentRequestDTO){
        studentService.nextYear(studentRequestDTO);
    }


}
