package com.example.cap.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Lands {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Please enter user id")
    @Positive(message = "User id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer userId;


    @NotBlank(message = "Please enter location")
    @Size(max = 100, message = "Location must not exceed 100 characters")
    @Column(columnDefinition = "varchar(100) not null")
    private String location;

    @NotNull(message = "Please enter area")
    @Positive(message = "Area must be greater than zero")
    @Column(columnDefinition = "decimal(12,2) not null check (area > 0)")
    private BigDecimal area;

    @NotNull(message = "Please enter length")
    @Positive(message = "Length must be greater than zero")
    @Column(columnDefinition = "decimal(10,2) not null check (length > 0)")
    private BigDecimal length;


    @NotNull(message = "Please enter width")
    @Positive(message = "Width must be greater than zero")
    @Column(columnDefinition = "decimal(10,2) not null check (width > 0)")
    private BigDecimal width;



}
