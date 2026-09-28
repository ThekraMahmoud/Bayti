package com.example.cap.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class User {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;


    @NotBlank(message = "please enter your name")
    @Size(max = 10)
    @Column(columnDefinition = "varchar(10) not null")
    private String name;


    @Email(message = "please check your email and enter correct email")
    @NotBlank(message = "please enter your email")
    @Column(columnDefinition = "varchar(30) not null unique")
    private String email;



    @NotBlank(message = "enter your phone please")
    @Pattern(
            regexp = "^(05\\d{8}|9665\\d{8})$",
            message = "Please enter a valid Saudi phone number "
    )
    @Column(columnDefinition = "varchar (12) not null unique ")
    private String phone;


    @NotBlank(message = "please enter password ")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$!%*?&])[A-Za-z\\d@#$!%*?&]{8,}$",
            message = "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character"
    )
    @Column(columnDefinition = "varchar(30) not null")
    private String password;


    @Column(columnDefinition = "varchar(10) not null")
    private String role;}
