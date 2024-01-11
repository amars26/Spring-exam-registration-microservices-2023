package com.amars.studentservice.service;

import com.amars.studentservice.dto.StudentDTO;
import com.amars.studentservice.dto.StudentRequestDTO;
import com.amars.studentservice.dto.StudentResponseDTO;
import com.amars.studentservice.dto.StudentResponseFullDTO;
import com.amars.studentservice.model.Student;
import com.amars.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public void createOrUpdateStudent(StudentDTO studentDTO){

        Student studentToSave = Student.builder()
                .studentCode(studentDTO.getStudentCode())
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .identificationNumber(studentDTO.getIdentificationNumber())
                .birthDate(studentDTO.getBirthDate())
                .year(studentDTO.getYear())
                .department(studentDTO.getDepartment())
                .major(studentDTO.getMajor())
                .status(studentDTO.getStatus())
                .build();

        studentRepository.save(studentToSave);

    }

    public StudentResponseDTO studentinfo(StudentRequestDTO studentRequestDTO){
        Optional<Student> studentOpt = studentRepository.findById(studentRequestDTO.getStudentCode());
        if(studentOpt.isPresent()) {
            Student student = studentOpt.get();
            return StudentResponseDTO.builder()
                    .firstName(student.getFirstName())
                    .lastName(student.getLastName())
                    .birthDate(student.getBirthDate())
                    .year(student.getYear())
                    .department(student.getDepartment())
                    .major(student.getMajor())
                    .status(student.getStatus())
                    .build();
        } else {
            return StudentResponseDTO.builder()
                    .firstName(" ")
                    .lastName(" ")
                    .birthDate(" ")
                    .year(0)
                    .department(" ")
                    .major(" ")
                    .status(" ")
                    .build();
        }
    }

    public StudentResponseFullDTO fullStudentInfo(StudentRequestDTO studentRequestDTO){
        Optional<Student> studentOpt = studentRepository.findById(studentRequestDTO.getStudentCode());
        Student student = studentOpt.get();
        return StudentResponseFullDTO.builder()
                .studentCode(student.getStudentCode())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .identificationNumber(student.getIdentificationNumber())
                .birthDate(student.getBirthDate())
                .year(student.getYear())
                .department(student.getDepartment())
                .major(student.getMajor())
                .status(student.getStatus())
                .build();
    }

    public void nextYear(StudentRequestDTO studentRequestDTO){
        Optional<Student> studentOpt = studentRepository.findById(studentRequestDTO.getStudentCode());
        Student student = studentOpt.get();
        student.setYear(student.getYear()+1);
        studentRepository.save(student);
    }



}
