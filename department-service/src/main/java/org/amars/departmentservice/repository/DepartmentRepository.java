package org.amars.departmentservice.repository;

import org.amars.departmentservice.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT d FROM Department d WHERE d.name = :departmentName")
    Department findByName(String departmentName);

}
