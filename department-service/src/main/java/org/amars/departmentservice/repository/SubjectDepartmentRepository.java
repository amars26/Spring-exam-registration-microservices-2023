package org.amars.departmentservice.repository;

import org.amars.departmentservice.model.SubjectDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectDepartmentRepository  extends JpaRepository<SubjectDepartment, Long> {

    @Query("SELECT sd FROM SubjectDepartment sd WHERE sd.departmentId = :departmentId AND sd.year = :year")
    List<SubjectDepartment> findAllByDepartment(Long departmentId, Long year);

}
