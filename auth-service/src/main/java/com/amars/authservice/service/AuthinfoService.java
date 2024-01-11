package com.amars.authservice.service;

import com.amars.authservice.dto.AuthinfoCreateDTO;
import com.amars.authservice.dto.AuthinfoRequestDTO;
import com.amars.authservice.dto.AuthinfoResponseDTO;
import com.amars.authservice.model.Authinfo;
import com.amars.authservice.repository.AuthinfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthinfoService {

    private final AuthinfoRepository authinfoRepository;

    public void createAuthinfo(AuthinfoCreateDTO authinfoCreateDTO){

        AuthinfoRequestDTO authinfoRequestDTO = new AuthinfoRequestDTO(authinfoCreateDTO.getCurrent_user());

        if(authenticateUser(authinfoRequestDTO).getRole().equals("admin")) {
            Authinfo authinfoToSave = Authinfo.builder()
                    .user_code(authinfoCreateDTO.getUser_code())
                    .role(authinfoCreateDTO.getRole())
                    .build();
            authinfoRepository.save(authinfoToSave);
        } else throw new IllegalArgumentException("Authorization error");
    }

    public AuthinfoResponseDTO authenticateUser(AuthinfoRequestDTO authinfoRequestDTO){
        Optional<Authinfo> optionalUser = authinfoRepository.findById(authinfoRequestDTO.getUser_code());
        AuthinfoResponseDTO authinfoResponseDTO = new AuthinfoResponseDTO();
        if(optionalUser.isPresent())
            authinfoResponseDTO.setRole(optionalUser.get().getRole());
        else authinfoResponseDTO.setRole("none");
        return authinfoResponseDTO;
    }



}
