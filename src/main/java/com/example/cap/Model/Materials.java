package com.example.cap.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Please enter construction phase id")
    @Positive(message = "Construction phase id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer constructionPhaseId;


    @NotBlank(message = "Please enter material type")
    @Size(max = 50, message = "Material type must not exceed 50 characters")
    @Column(columnDefinition = "varchar(50) not null")
    private String materialType;


    @NotNull(message = "Please enter planned quantity")
    @Positive(message = "Planned quantity must be greater than zero")
    @Column(columnDefinition = "int not null check (planned_quantity > 0)")
    private Integer plannedQuantity;

    @NotNull(message = "Please enter purchased quantity")
    @Positive(message = "purchased quantity must be greater than zero")
    @Column(columnDefinition = "int not null check (purchased_quantity > 0)")
    private Integer purchasedQuantity;



    @NotNull(message = "Please enter used quantity")
    @PositiveOrZero(message = "Used quantity cannot be negative")
    @Column(columnDefinition = "int not null check (used_quantity >= 0)")
    private Integer usedQuantity;

    @Column(columnDefinition = "int not null check (surplus_quantity >= 0)")
    private Integer surplusQuantity;
}