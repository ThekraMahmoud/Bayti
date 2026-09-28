package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.SurplusOrders;
import com.example.cap.Service.SurplusOrdersService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/surplus/orders")
@AllArgsConstructor
public class SurplusOrdersController {

    private final SurplusOrdersService surplusOrdersService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody SurplusOrders surplusOrders, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        String check = surplusOrdersService.add(surplusOrders);
        return switch (check) {
            case "Surplus item not found",
                 "Buyer not found",
                 "You cannot buy your own surplus",
                 "Surplus item is not available",
                 "Quantity exceeds available surplus" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Surplus order created successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/pay/buyerId/{buyerId}/surplusOrderId/{surplusOrderId}")
    public ResponseEntity<?> pay(@PathVariable Integer buyerId, @PathVariable Integer surplusOrderId) {

        String check = surplusOrdersService.pay(buyerId, surplusOrderId);
        return switch (check) {

            case "Surplus order not found",
                 "You do not own this order",
                 "Order is already paid",
                 "Surplus item not found",
                 "Quantity exceeds available surplus" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Surplus order paid successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}