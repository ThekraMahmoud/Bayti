package com.example.cap.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class ConstructionPhases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(columnDefinition = "int not null")
    @NotNull(message = "Enter the offer id ")
    @Positive(message = "jest allow positive number")
    private Integer offersId;


    @Column (columnDefinition = "varchar (50) not null ")
    @Size(max = 50 ,message = "the phase name is too long , please enter a maximum of 50 characters  ")
    @NotBlank(message = "please enter phase  name ")
    private String phaseName;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedEndDate;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualEndDate;

    @Pattern(regexp = "NOT DONE|IN PROGRESS|COMPLETED"
            ,message = "jest allow to enter one of this : NOT DONE or IN PROGRESS or COMPLETED")
    @Column(columnDefinition = "varchar (15) not null check (status='NOT DONE' or  status='IN PROGRESS' or status='COMPLETED')")
    private String status;

}
