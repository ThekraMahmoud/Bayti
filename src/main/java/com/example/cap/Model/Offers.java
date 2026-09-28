package com.example.cap.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Offers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Please enter project id")
    @Positive(message = "Project id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer projectId;


    @NotNull(message = "Please enter contractor id")
    @Positive(message = "Contractor id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer contractorId;


    @NotNull(message = "Please enter price")
    @Positive(message = "Price must be greater than zero")
    @Column(columnDefinition = "decimal(12,2) not null check (price > 0)")
    private BigDecimal price;


    @NotNull(message = "Please enter start date")
    @Column(columnDefinition = "DATE not null")
    private LocalDate startDate;


    @NotNull(message = "Please enter expected end date")
    @Column(columnDefinition = "DATE not null")
    private LocalDate expectedEndDate;


    @Column(columnDefinition = "int not null check (duration > 0)")
    private Integer duration;



//    @Pattern(
//            regexp = "^(PENDING|ACCEPTED|REJECTED|CANCELLED)$",
//            message = "Status must be PENDING, ACCEPTED, or REJECTED "
//    )
    @Column(
            columnDefinition = "varchar(10) not null check (status in ('PENDING','ACCEPTED','REJECTED','CANCELLED'))"
    )
    private String status="PENDING";
}