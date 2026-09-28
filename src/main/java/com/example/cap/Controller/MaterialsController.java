package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.Materials;
import com.example.cap.Service.MaterialsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/materials")
@AllArgsConstructor
public class MaterialsController {

    private final MaterialsService materialsService;


    @GetMapping("/get/phaseId/{phaseId}")
    public ResponseEntity<?> get(@PathVariable Integer phaseId) {

        return ResponseEntity.status(200)
                .body(materialsService.get(phaseId));
    }


    @PostMapping("/add/contractorsId/{contractorsId}")
    public ResponseEntity<?> add(@PathVariable Integer contractorsId, @Valid @RequestBody Materials materials, Errors errors) {

        if (errors.hasErrors()) {
            return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));
        }

        String check = materialsService.add(contractorsId, materials);
        return switch (check) {

            case "Contractor not found",
                 "Construction phase not found",
                 "Offer not found",
                 "You do not own this construction phase",
                 "Materials can only be added while the phase is in progress",
                 "Purchased quantity cannot be less than used quantity" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Material added successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping ("/purchase/contractorsId/{contractorsId}/materialsId/{materialsId}/count/{count}")

    public ResponseEntity<?> increasePurchasedQuantity(@PathVariable Integer contractorsId, @PathVariable Integer materialsId, @PathVariable Integer count) {
        String check = materialsService.increasePurchasedQuantity(contractorsId, materialsId, count);

        return switch (check) {

            case "Contractor not found",
                 "Material not found",
                 "Construction phase not found",
                 "Offer not found",
                 "Material can only be updated while the phase is in progress",
                 "You do not own this material",
                 "Quantity must be greater than zero" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Material quantity increased successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/use/contractorsId/{contractorsId}/materialsId/{materialsId}/count/{count}")
    public ResponseEntity<?> increaseUsedQuantity(@PathVariable Integer contractorsId, @PathVariable Integer materialsId, @PathVariable Integer count) {
        String check = materialsService.increaseUsedQuantity(contractorsId, materialsId, count);

        return switch (check) {

            case "Contractor not found",
                 "Material not found",
                 "Construction phase not found",
                 "Offer not found",
                 "You do not own this construction phase",
                 "Material can only be updated while the phase is in progress",
                 "Quantity must be greater than zero",
                 "Used quantity cannot be greater than purchased quantity. Please purchase more materials first" -> ResponseEntity.status(400).body(new ApiResponse(check));
            case "Material quantity used successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}