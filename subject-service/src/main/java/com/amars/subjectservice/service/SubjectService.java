package com.amars.subjectservice.service;

import com.amars.subjectservice.dto.*;
import com.amars.subjectservice.model.StudentSubject;
import com.amars.subjectservice.model.Subject;
import com.amars.subjectservice.repository.StudentSubjectRepository;
import com.amars.subjectservice.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    public void createOrUpdateSubject(SubjectDTO subjectDTO){
        Subject subject = subjectRepository.findByName(subjectDTO.getName());

        if(subject != null){
            Subject subjectToSave = Subject.builder()
                    .id(subject.getId())
                    .name(subjectDTO.getName())
                    .description(subjectDTO.getDescription())
                    .professor(subjectDTO.getProfessor())
                    .professorCode(subjectDTO.getProfessorCode())
                    .major(subjectDTO.getMajor())
                    .build();

            subjectRepository.save(subjectToSave);
        } else {
            Subject subjectToSave = Subject.builder()
                    .name(subjectDTO.getName())
                    .description(subjectDTO.getDescription())
                    .professor(subjectDTO.getProfessor())
                    .professorCode(subjectDTO.getProfessorCode())
                    .major(subjectDTO.getMajor())
                    .build();

            subjectRepository.save(subjectToSave);
        }
    }

    public List<SubjectSpecialDTO> getAllSubjects() {
        List<Subject> departments = subjectRepository.findAll();
        return departments.stream().map(this::mapToSubjectSpecialDTO).toList();
    }

    public SubjectDTO findSubjectByName(SubjectRequestDTO subjectRequestDTO){
        Subject subject = subjectRepository.findByName(subjectRequestDTO.getName());
        return mapToSubjectDTO(subject);
    }

    private SubjectDTO mapToSubjectDTO(Subject subjectToConvert) {
        return SubjectDTO.builder()
                .name(subjectToConvert.getName())
                .description(subjectToConvert.getDescription())
                .professor(subjectToConvert.getProfessor())
                .professorCode(subjectToConvert.getProfessorCode())
                .major(subjectToConvert.getMajor())
                .build();
    }

    private SubjectSpecialDTO mapToSubjectSpecialDTO(Subject subjectToConvert) {
        return SubjectSpecialDTO.builder()
                .id(subjectToConvert.getId())
                .name(subjectToConvert.getName())
                .description(subjectToConvert.getDescription())
                .professor(subjectToConvert.getProfessor())
                .professorCode(subjectToConvert.getProfessorCode())
                .major(subjectToConvert.getMajor())
                .build();
    }

    public void createOrUpdateStudentSubject(StudentSubjectDTO studentSubjectDTO){

        StudentSubject studentSubject = studentSubjectRepository.findByStudentAndSubject(studentSubjectDTO.getStudentCode(),studentSubjectDTO.getSubject());

        if(studentSubject != null){
            StudentSubject studentSubjectToSave = StudentSubject.builder()
                    .id(studentSubject.getId())
                    .studentCode(studentSubject.getStudentCode())
                    .subject(studentSubject.getSubject())
                    .score(studentSubjectDTO.getScore())
                    .grade(studentSubjectDTO.getGrade())
                    .date(studentSubjectDTO.getDate())
                    .complete(studentSubjectDTO.isComplete())
                    .build();

            studentSubjectRepository.save(studentSubjectToSave);
        } else {
            StudentSubject studentSubjectToSave = StudentSubject.builder()
                    .studentCode(studentSubjectDTO.getStudentCode())
                    .subject(studentSubjectDTO.getSubject())
                    .score(studentSubjectDTO.getScore())
                    .grade(studentSubjectDTO.getGrade())
                    .date(studentSubjectDTO.getDate())
                    .complete(studentSubjectDTO.isComplete())
                    .build();

            studentSubjectRepository.save(studentSubjectToSave);
        }

    }

    public List<StudentSubjectDTO> showCompletedSubjectsForStudent(StudentSubjectRequestDTO studentSubjectRequestDTO){
        List<StudentSubject> subjects = studentSubjectRepository.findAllComplete(studentSubjectRequestDTO.getStudentCode());
        return subjects.stream().map(this::mapToStudentSubjectDTO).toList();
    }

    private StudentSubjectDTO mapToStudentSubjectDTO(StudentSubject studentSubjectToConvert) {
        return StudentSubjectDTO.builder()
                .studentCode(studentSubjectToConvert.getStudentCode())
                .subject(studentSubjectToConvert.getSubject())
                .score(studentSubjectToConvert.getScore())
                .grade(studentSubjectToConvert.getGrade())
                .date(studentSubjectToConvert.getDate())
                .complete(studentSubjectToConvert.isComplete())
                .build();
    }

    public List<StudentSubjectDTO> showOngoingSubjectsForStudent(StudentSubjectRequestDTO studentSubjectRequestDTO){
        List<StudentSubject> subjects = studentSubjectRepository.findAllIncomplete(studentSubjectRequestDTO.getStudentCode());
        return subjects.stream().map(this::mapToStudentSubjectDTO).toList();
    }

    public List<StudentSubjectSpecialDTO> getAllStudentsOnSubjects() {
        List<StudentSubject> subjects = studentSubjectRepository.findAll();
        return subjects.stream().map(this::mapToStudentSubjectSpecialDTO).toList();
    }

    private StudentSubjectSpecialDTO mapToStudentSubjectSpecialDTO(StudentSubject subjectToConvert) {
        return StudentSubjectSpecialDTO.builder()
                .id(subjectToConvert.getId())
                .studentCode(subjectToConvert.getStudentCode())
                .subject(subjectToConvert.getSubject())
                .score(subjectToConvert.getScore())
                .grade(subjectToConvert.getGrade())
                .date(subjectToConvert.getDate())
                .complete(subjectToConvert.isComplete())
                .build();
    }

}
