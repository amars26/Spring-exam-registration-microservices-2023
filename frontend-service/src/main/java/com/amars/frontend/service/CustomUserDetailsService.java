package com.amars.frontend.service;

import com.amars.frontend.model.CustomUserDetails;
import com.amars.frontend.model.User;
import com.amars.frontend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username);

        if(user!=null) {
            return new CustomUserDetails(user);
        } else throw new UsernameNotFoundException("User not found");
    }

}
