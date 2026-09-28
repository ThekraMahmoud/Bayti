package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @PutMapping("/approve/{email}")
    public ResponseEntity<?> approveContractor(@PathVariable String email) {

        boolean check = adminService.approveContractor(email);

        if (!check) {
            return ResponseEntity.status(400).body(new ApiResponse("Contractor not found"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Contractor approved successfully"));
    }


    @DeleteMapping("/reject/{email}")
    public ResponseEntity<?> rejectContractor(@PathVariable String email) {

        boolean check = adminService.rejectContractor(email);

        if (!check) {
            return ResponseEntity.status(400).body("Contractor not found");
        }
        return ResponseEntity.status(200).body("Contractor rejected successfully");
    }



    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {

        return ResponseEntity.status(200).body(adminService.getUser());
    }


    @GetMapping("/contractors")
    public ResponseEntity<?> getContractors() {
        return ResponseEntity.status(200).body(adminService.getContractors());
    }

    @GetMapping("/contractors/pending")
    public ResponseEntity<?> getPendingContractors() {
        return ResponseEntity.status(200).body(adminService.getPendingContractors());
    }


    @DeleteMapping("/user/{email}")
    public ResponseEntity<?> deleteUser(@PathVariable String email) {

        String check = adminService.deleteUser(email);

        return switch (check) {case "User not found" -> ResponseEntity.status(400).body(new ApiResponse("User not found"));

            case "You cannot delete the primary admin" -> ResponseEntity.status(400).body(new ApiResponse("You cannot delete the primary admin"));

            case "User deleted successfully" -> ResponseEntity.status(200).body(new ApiResponse("User deleted successfully"));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/add-admin/{email}")
    public ResponseEntity<?> addAdmin(@PathVariable String email) {

        String check = adminService.addAdmin(email);

        return switch (check) {case "not found" -> ResponseEntity.status(400).body(new ApiResponse("User not found"));

            case "already admin account" -> ResponseEntity.status(400).body(new ApiResponse("Already admin account"));

            case "User promoted to admin successfully" -> ResponseEntity.status(200).body(new ApiResponse("User promoted to admin successfully"));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @DeleteMapping("/contractor/{email}")
    public ResponseEntity<?> deleteContractor(@PathVariable String email) {

        boolean check = adminService.deleteContractors(email);

        if (!check) {
            return ResponseEntity.status(400).body(new ApiResponse("Contractor not found"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("Contractor deleted successfully"));
    }
}
