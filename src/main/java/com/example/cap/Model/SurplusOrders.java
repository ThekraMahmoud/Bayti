package com.example.cap.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurplusOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @NotNull(message = "Please enter surplus item id")
    @Positive(message = "Surplus item id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer surplusItemId;

    // المشتري
    @NotNull(message = "Please enter buyer id")
    @Positive(message = "Buyer id must be greater than zero")
    @Column(columnDefinition = "int not null")
    private Integer buyerId;


    @NotNull(message = "Please enter quantity")
    @Positive(message = "Quantity must be greater than zero")
    @Column(columnDefinition = "int not null check (quantity > 0)")
    private Integer quantity;



    @Column(columnDefinition = "boolean not null")
    private Boolean paid = false;
}
