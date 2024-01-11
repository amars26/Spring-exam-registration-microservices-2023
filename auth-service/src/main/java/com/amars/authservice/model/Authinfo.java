package com.amars.authservice.model;

import lombok.*;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="authinfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Authinfo {

    @Id
    private String user_code;

    private String role;

}
