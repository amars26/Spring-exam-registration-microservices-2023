package org.amars.departmentservice.controller;

import lombok.RequiredArgsConstructor;
import org.amars.departmentservice.dto.CreateSubjectDepartmentDTO;
import org.amars.departmentservice.dto.DepartmentDTO;
import org.amars.departmentservice.dto.SubjectDTO;
import org.amars.departmentservice.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/createDepartment")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDepartment(@RequestBody DepartmentDTO departmentDTO){
        departmentService.createOrUpdateDepartment(departmentDTO);
    }

    @GetMapping("/departments")
    @ResponseStatus(HttpStatus.OK)
    public List<DepartmentDTO> getAllDepartments(){
        return departmentService.getAllDepartments();
    }

    @PostMapping("/createSubject")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSubject(@RequestBody CreateSubjectDepartmentDTO createSubjectDepartmentDTO){
        departmentService.addSubjectToDepartment(createSubjectDepartmentDTO);
    }


    @PostMapping("/subjects")
    @ResponseStatus(HttpStatus.OK)
    public List<SubjectDTO> getAllSubjects(@RequestBody SubjectDTO dep){
        return departmentService.getAllSubjects(dep);
    }

}
