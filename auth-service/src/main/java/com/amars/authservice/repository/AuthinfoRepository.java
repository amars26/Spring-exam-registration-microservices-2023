package com.amars.authservice.repository;

import com.amars.authservice.model.Authinfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthinfoRepository extends JpaRepository<Authinfo, String> {
}
