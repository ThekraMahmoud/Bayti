package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.Contractors;
import com.example.cap.Service.ContractorsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contractor")
@AllArgsConstructor
public class ContractorsController {

    private final ContractorsService contractorsService;

    @GetMapping("/get")
    public ResponseEntity<?> get() {
        return ResponseEntity.status(200).body(contractorsService.gatContractors());
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid Contractors contractors, Errors errors) {
        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }

        String check = contractorsService.addContractors(contractors);
        return switch (check) {
            case "Email already used" -> ResponseEntity.status(400).body(new ApiResponse("Email already used"));

            case "Phone already used" -> ResponseEntity.status(400).body(new ApiResponse("Phone already used"));

            case "License Number already used" -> ResponseEntity.status(400).body(new ApiResponse("License Number already used"));

            case "Verification code sent to your email" -> ResponseEntity.status(200).body(new ApiResponse("Verification code sent to your email"));

            default -> ResponseEntity.status(400).body(new ApiResponse("somethings errors"));
        };
    }

    @PutMapping("/verifyEmail/{email}/{code}")
    public ResponseEntity<?> verifyEmail(
            @PathVariable String email,
            @PathVariable String code) {

        String check = contractorsService.verifyEmail(email, code);

        return switch (check) {
            case "Invalid email or verification code" -> ResponseEntity.status(400).body(new ApiResponse("Invalid email or verification code"));
            case "Verification code expired" -> ResponseEntity.status(400).body(new ApiResponse("Verification code expired"));
            case "Registration data not found" -> ResponseEntity.status(400).body(new ApiResponse("Registration data not found"));
            case "Email verified successfully . Your account is pending admin approval" -> ResponseEntity.status(200).body(new ApiResponse("Email verified successfully . Your account is pending admin approval"));
            default -> ResponseEntity.status(400).body(new ApiResponse("somethings errors"));
        };
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody  Contractors contractors, Errors errors) {

//        if(errors.hasErrors()){
//            String message=errors.getFieldError().getDefaultMessage();
//            return ResponseEntity.status(400).body(message);
//        }
        String check = contractorsService.update(id, contractors);

        return switch (check) {
            case "Contractor not found" -> ResponseEntity.status(400).body(new ApiResponse("Contractor not found"));

            case "Account is not active" -> ResponseEntity.status(400).body(new ApiResponse("Account is not active"));

            case "Contractor updated successfully" -> ResponseEntity.status(200).body(new ApiResponse("Contractor updated successfully"));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {

        String check = contractorsService.delete(id);

        return switch (check) {
            case "Contractor not found" -> ResponseEntity.status(400).body(new ApiResponse("Contractor not found"));
            case "Account is not active" -> ResponseEntity.status(400).body(new ApiResponse("Account is not active"));
            case "Contractor deleted successfully" -> ResponseEntity.status(200).body(new ApiResponse("Contractor deleted successfully"));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}

