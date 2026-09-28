package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.SurplusItems;
import com.example.cap.Service.SurplusItemsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/surplus")
@AllArgsConstructor
public class SurplusItemsController {

    private final SurplusItemsService surplusItemsService;


    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(surplusItemsService.getAll());
    }


    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody SurplusItems surplusItems, Errors errors) {

        if (errors.hasErrors()) {
            String message=errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        String check = surplusItemsService.add(surplusItems);
        return switch (check) {
            case "Material not found",
                 "Construction phase not found",
                 "Surplus can only be listed after the construction phase is completed",
                 "There is no surplus available",
                 "Offer not found",
                 "Construction project not found",
                 "User not found",
                 "Surplus item already exists for this material",
                 "You do not own this project" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Surplus item added successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/update/sellerId/{sellerId}/surplusItemsId/{surplusItemsId}")
    public ResponseEntity<?> update(@PathVariable Integer sellerId, @PathVariable Integer surplusItemsId,  @RequestBody SurplusItems updateSurplusItems, Errors errors) {

//        if (errors.hasErrors()) {
//            String message=errors.getFieldError().getDefaultMessage();
//            return ResponseEntity.status(400).body(new ApiResponse(message));
//        }

        Boolean check = surplusItemsService.update(sellerId, surplusItemsId, updateSurplusItems
        );

        if (!check) {
            return ResponseEntity.status(400).body(new ApiResponse("Surplus item not found"));
        }

        return ResponseEntity.status(200).body(new ApiResponse("Surplus item updated successfully"));
    }


    @PutMapping("/close/sellerId/{userId}/surplusItemsId/{surplusItemsId}")
    public ResponseEntity<?> close(@PathVariable Integer userId, @PathVariable Integer surplusItemsId) {

        String check = surplusItemsService.closed(userId, surplusItemsId);

        return switch (check) {

            case "Surplus item not found",
                 "Sold surplus item cannot be closed",
                 "Surplus item is already closed" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Surplus item closed successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }

    @PutMapping("/reopen/sellerId/{userId}/surplusItemsId/{surplusItemsId}")
    public ResponseEntity<?> reopen(@PathVariable Integer userId, @PathVariable Integer surplusItemsId) {

        String check = surplusItemsService.reopen(userId, surplusItemsId);

        return switch (check) {

            case "Surplus item not found",
                 "Sold surplus item cannot be reopened",
                 "Surplus item is already available" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Surplus item reopened successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }

    @PutMapping("/transfer/userId/{userId}/surplusItemsId/{surplusItemsId}/materialsId/{materialsId}/quantity/{quantity}")
    public ResponseEntity<?> transfer(@PathVariable Integer userId, @PathVariable Integer surplusItemsId, @PathVariable Integer materialsId, @PathVariable Integer quantity) {

        String check = surplusItemsService.transfer(userId, surplusItemsId, materialsId, quantity);

        return switch (check) {
            case "Surplus item not found",
                 "Surplus item is not available",
                 "Material not found",
                 "Construction phase not found",
                 "Offer not found",
                 "Construction project not found",
                 "You do not own this material",
                 "Material type must be the same",
                 "Quantity must be greater than zero",
                 "Quantity exceeds available surplus" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Surplus shared successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}