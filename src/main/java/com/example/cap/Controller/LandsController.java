package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.Lands;
import com.example.cap.Service.LandsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lands")
@AllArgsConstructor
public class LandsController {

    private final LandsService landsService;

//هنا فقط الي له صلاحية يشوف الارض المستخدم صاحب الارض وليس كل المستخدمين
    @GetMapping("/user/Landes/{id}")
    public ResponseEntity<?> getLandes(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(landsService.getLandes(id));
    }


    @PostMapping("/user/Landes/add")
    public ResponseEntity<?> add(@RequestBody @Valid Lands lands, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        String check = landsService.add(lands);

        return switch (check) {

            case "User not found" -> ResponseEntity.status(404).body(new ApiResponse(check));

            case "Admin users are not allowed to add lands" -> ResponseEntity.status(403).body(new ApiResponse(check));

            case "Land added successfully" -> ResponseEntity.status(201).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/user/Landes/update/user/{userId}/land/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @PathVariable Integer userId,  @RequestBody Lands landsUpdate, Errors errors) {

//        if (errors.hasErrors()) {
//            String message = errors.getFieldError().getDefaultMessage();
//            return ResponseEntity.status(400).body(new ApiResponse(message));
//        }

        String check = landsService.update(userId, id, landsUpdate);

        return switch (check) {

            case "Land not found" -> ResponseEntity.status(404).body(new ApiResponse(check));
            case "You do not own this land" -> ResponseEntity.status(400).body(new ApiResponse(check));
            case "Land cannot be updated because it is currently in use" -> ResponseEntity.status(400).body(new ApiResponse(check));
            case "Land updated successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @DeleteMapping("/user/Landes/delete/user/{userId}/land/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer userId) {

        String check = landsService.delete(userId, id);

        return switch (check) {

            case "Land not found" -> ResponseEntity.status(404).body(new ApiResponse(check));

            case "You do not own this land" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Land cannot be deleted because it is currently in use" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Land deleted successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}