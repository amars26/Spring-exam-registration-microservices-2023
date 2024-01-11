package com.amars.authservice.controller;

import com.amars.authservice.dto.AuthinfoCreateDTO;
import com.amars.authservice.dto.AuthinfoRequestDTO;
import com.amars.authservice.dto.AuthinfoResponseDTO;
import com.amars.authservice.service.AuthinfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthinfoController {

    private final AuthinfoService authinfoService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAuthinfo(@RequestBody AuthinfoCreateDTO authinfoCreateDTO){
        authinfoService.createAuthinfo(authinfoCreateDTO);
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public AuthinfoResponseDTO authenticateUser(@RequestBody AuthinfoRequestDTO authinfoRequestDTO){
        return authinfoService.authenticateUser(authinfoRequestDTO);
    }

}
