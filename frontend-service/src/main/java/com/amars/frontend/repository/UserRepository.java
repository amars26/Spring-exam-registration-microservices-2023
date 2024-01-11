package com.amars.frontend.repository;

import com.amars.frontend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username =?1")
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role != 'student'")
    List<User> findAllUsers();

    @Query("SELECT u FROM User u WHERE u.role = 'student'")
    List<User> findAllStudents();

}
