package org.amars.departmentservice.service;

import lombok.RequiredArgsConstructor;
import org.amars.departmentservice.dto.DepartmentDTO;
import org.amars.departmentservice.dto.CreateSubjectDepartmentDTO;
import org.amars.departmentservice.dto.SubjectDTO;
import org.amars.departmentservice.model.Department;
import org.amars.departmentservice.model.SubjectDepartment;
import org.amars.departmentservice.repository.DepartmentRepository;
import org.amars.departmentservice.repository.SubjectDepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final SubjectDepartmentRepository subjectDepartmentRepository;

    public void createOrUpdateDepartment(DepartmentDTO departmentDTO){

        Department departmentId = departmentRepository.findByName(departmentDTO.getName());


        if(departmentId != null){
            Department departmentToSave = Department.builder()
                    .id(departmentId.getId())
                    .name(departmentDTO.getName())
                    .description(departmentDTO.getDescription())
                    .build();

            departmentRepository.save(departmentToSave);
        } else {
            Department departmentToSave = Department.builder()
                    .name(departmentDTO.getName())
                    .description(departmentDTO.getDescription())
                    .build();

            departmentRepository.save(departmentToSave);
        }

    }

    public void addSubjectToDepartment(CreateSubjectDepartmentDTO createSubjectDepartmentDTO){

        Department departmentId = departmentRepository.findByName(createSubjectDepartmentDTO.getDepartmentName());


        SubjectDepartment subjectToAdd = SubjectDepartment.builder()
                .name(createSubjectDepartmentDTO.getName())
                .departmentId(departmentId.getId())
                .year(createSubjectDepartmentDTO.getYear())
                .build();

        subjectDepartmentRepository.save(subjectToAdd);

    }

    public List<DepartmentDTO> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream().map(this::mapToDepartmentDTO).toList();

    }

    private DepartmentDTO mapToDepartmentDTO(Department departmentToConvert) {
        return DepartmentDTO.builder()
                .name(departmentToConvert.getName())
                .description(departmentToConvert.getDescription())
                .build();
    }

    public List<SubjectDTO> getAllSubjects(SubjectDTO subjectDTO) {
        Long departmentId = departmentRepository.findByName(subjectDTO.getName()).getId();
        List<SubjectDepartment> subjects = subjectDepartmentRepository.findAllByDepartment(departmentId, subjectDTO.getYear());
        return subjects.stream().map(this::mapToSubjectDTO).toList();

    }

    private SubjectDTO mapToSubjectDTO(SubjectDepartment subjectToConvert) {
        return SubjectDTO.builder()
                .name(subjectToConvert.getName())
                .year(subjectToConvert.getYear().longValue())
                .build();
    }



}
