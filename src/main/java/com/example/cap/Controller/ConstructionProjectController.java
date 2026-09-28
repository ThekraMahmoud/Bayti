package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.ConstructionProject;
import com.example.cap.Service.ConstructionProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project")
@AllArgsConstructor
public class ConstructionProjectController {

    private final ConstructionProjectService constructionProjectService;


    // كل مشاريع البناء تظهر للمستخدمين
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(constructionProjectService.getAll());
    }


    // فقط مشاريع المستخدم
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> get(@PathVariable Integer userId) {
        return ResponseEntity.status(200).body(constructionProjectService.get(userId));
    }


    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ConstructionProject project, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        String check = constructionProjectService.add(project);

        return switch (check) {
            case "lands id not found",
                 "user not found",
                 "You do not own this land",
                 "This land already has a construction project" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "add successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/update/userId/{userId}/projectId/{projectId}")
    public ResponseEntity<?> update(@PathVariable Integer userId, @PathVariable Integer projectId,  @RequestBody ConstructionProject updateProject, Errors errors) {
//        if (errors.hasErrors()) {
//            String message = errors.getFieldError().getDefaultMessage();
//            return ResponseEntity.status(400).body(new ApiResponse(message));
//        }

        String check = constructionProjectService.update(userId, projectId, updateProject);

        return switch (check) {
            case "Construction project not found",
                 "You do not own this project",
                 "Construction project cannot be updated because it already has offers or has been accepted" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Construction project updated successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @DeleteMapping("/delete/userId/{userId}/projectId/{projectId}")
    public ResponseEntity<?> delete(@PathVariable Integer userId, @PathVariable Integer projectId) {

        String check = constructionProjectService.delete(userId, projectId);

        return switch (check) {

            case "Construction project not found",
                 "You do not own this project",
                 "Construction project cannot be deleted because an offer has already been accepted" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Construction project Delete successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}