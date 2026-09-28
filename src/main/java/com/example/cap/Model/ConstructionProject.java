package com.example.cap.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ConstructionProject {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @NotNull(message = "please enter land id")
        @Column(columnDefinition = "int not null")
        private Integer landId;

        @NotNull(message = "please enter User id")
        @Column(columnDefinition = "int not null")
        private Integer userId;

        @NotBlank(message = "please enter project type")
        @Pattern(
                regexp = "^(VILLA|RESIDENTIAL_BUILDING|RESIDENTIAL_COMPLEX|COMMERCIAL_BUILDING|TRADITIONAL_HOUSE)$",
                message = "Please choose one: VILLA, RESIDENTIAL_BUILDING, RESIDENTIAL_COMPLEX, COMMERCIAL_BUILDING, or TRADITIONAL_HOUSE"
        )
        @Column(
                columnDefinition = "varchar(30) not null check (project_type in ('VILLA','RESIDENTIAL_BUILDING','RESIDENTIAL_COMPLEX','COMMERCIAL_BUILDING','TRADITIONAL_HOUSE'))"
        )
        private String projectType;

        @NotBlank(message = "please enter construction type")
        @Pattern(
                regexp = "^(NEW_CONSTRUCTION|RENOVATION|EXPANSION|ADDITION)$",
                message = "Please choose one: NEW_CONSTRUCTION, RENOVATION, EXPANSION, or ADDITION"
        )
        @Column(
                columnDefinition = "varchar(30) not null check (construction_type in ('NEW_CONSTRUCTION','RENOVATION','EXPANSION','ADDITION'))"
        )
        private String constructionType;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        @Column(columnDefinition = "varchar(500)")
        private String description;

        @NotNull(message = "please enter floor number")
        @PositiveOrZero(message = "jest allow positive number")
        @Column(columnDefinition = "int not null")
        private Integer floors;

        @NotNull(message = "please enter room number")
        @Positive(message = "jest allow positive number")
        @Column(columnDefinition = "int not null")
        private Integer room;

        @NotNull(message = "Please enter the budget")
        @Positive(message = "Budget must be greater than zero")
        @Column(columnDefinition = "decimal(12,2) not null check (budget > 0)")
        private BigDecimal budget;

        @CreationTimestamp
        private LocalDateTime createdAt;


        @Column(columnDefinition = "varchar(20) not null check (status in ('OPEN','HAS_OFFERS','ACCEPTED'))")
        private String status;
    }
