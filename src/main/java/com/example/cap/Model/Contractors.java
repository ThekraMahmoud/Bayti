package com.example.cap.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Contractors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotBlank(message = "enter your name please")
    @Size(max = 15)

    @Column(columnDefinition = "varchar (15) not null")
    private String name ;


    @Email(message = "please check your email and enter correct email")
    @NotBlank(message = "enter your email please")

    @Column(columnDefinition = "varchar(30) not null unique ")
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



    @NotBlank(message = "please enter company name")
    @Size(max = 15, message = "Please enter no more than 15 characters")
    @Column(columnDefinition = "varchar(15) not null  ")
    private String companyName;


    @NotNull(message = " how match experience do you have ")
    @PositiveOrZero(message = "Experience cannot be negative")
    @Column(columnDefinition = "int not null ")
    private Integer experience;


    @NotBlank(message = "please enter your location")
    @Size(max = 15, message = "Please enter no more than 15 characters")
    @Column(columnDefinition = "varchar(15) not null")
    private String location;


    @NotNull(message = "please enter license number ")
    @PositiveOrZero(message = "jest allow positive number")
    @Column(columnDefinition = "int not null unique")
    private Integer licenseNumber;

    @URL(message = "Please enter a valid URL")
    @Column(columnDefinition = "varchar (200) ")
    private String urlPortfolio;


    @Size(max = 300, message = "Bio must not exceed 300 characters")
    @Column(columnDefinition = "varchar (300) ")
    private String bio;


    @Column(nullable=false)
    private Boolean isVerified =false;

}
