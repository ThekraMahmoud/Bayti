package com.example.cap.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SurplusItems {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @NotNull(message = "Please enter material id")
    @Positive(message = "Material id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer materialId;


     //الشخص الي يبيع الفائض
    @NotNull(message = "Please enter seller id")
    @Positive(message = "Seller id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer sellerId;


    @Column(columnDefinition = "varchar(50) not null")
    private String materialType;

    @Column(columnDefinition = "int not null check (quantity >= 0)")
    private Integer quantity;


    @NotBlank(message = "Please enter description")
    @Size(max = 300, message = "Description must not exceed 300 characters")
    @Column(columnDefinition = "varchar(300) not null")
    private String description;


    @NotNull(message = "Please enter price")
    @Positive(message = "Price must be greater than zero")
    @Column(columnDefinition = "decimal(12,2) not null check (price >0)")
    private BigDecimal price;


    @Pattern(
            regexp = "^(AVAILABLE|CLOSED|SOLD)$",
            message = "Status must be AVAILABLE, CLOSED, or SOLD"
    )
    @Column(
            columnDefinition = "varchar(10) not null check (status in ('AVAILABLE','SOLD','CLOSED'))"
    )
    private String status = "AVAILABLE";
}