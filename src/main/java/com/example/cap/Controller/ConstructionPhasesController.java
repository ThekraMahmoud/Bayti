package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.ConstructionPhases;
import com.example.cap.Service.ConstructionPhasesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/phases")
@AllArgsConstructor
public class ConstructionPhasesController {

    private final ConstructionPhasesService constructionPhasesService;


    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ConstructionPhases phases, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        String check = constructionPhasesService.add(phases);

        return switch (check) {
            case "Offer not found",
                 "Offer is not accepted",
                 "Phase name already exists" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Phase added successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }



    @PutMapping("/update/contractorsId/{contractorsId}/offerId/{offerId}/phaseId/{phaseId}")
    public ResponseEntity<?> update(@PathVariable Integer contractorsId, @PathVariable Integer offerId, @PathVariable Integer phaseId, @RequestBody ConstructionPhases updatePhase, Errors errors) {

//        if (errors.hasErrors()) {
//            String message = errors.getFieldError().getDefaultMessage();
//            return ResponseEntity.status(400).body(new ApiResponse(message));
//        }

        String check = constructionPhasesService.updatePhase(contractorsId, offerId, phaseId, updatePhase);

        return switch (check) {

            case "Offer not found",
                 "You do not own this offer",
                 "Phase not found",
                 "This phase does not belong to this offer",
                 "Completed phase cannot be updated" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Phase updated successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }





    @PutMapping("/start/contractorsId/{contractorsId}/offerId/{offerId}/phaseId/{phaseId}")
    public ResponseEntity<?> startPhase(@PathVariable Integer contractorsId, @PathVariable Integer offerId, @PathVariable Integer phaseId) {

        String check = constructionPhasesService.startPhase(contractorsId, offerId, phaseId);

        return switch (check) {
            case "Offer not found",
                 "You do not own this offer",
                 "Phase not found",
                 "This phase does not belong to this offer",
                 "Phase has already started or completed" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Phase started successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/complete/contractorsId/{contractorsId}/offerId/{offerId}/phaseId/{phaseId}")
    public ResponseEntity<?> completePhase(@PathVariable Integer contractorsId, @PathVariable Integer offerId, @PathVariable Integer phaseId) {

        String check = constructionPhasesService.completePhase(contractorsId, offerId, phaseId);

        return switch (check) {
            case "Offer not found",
                 "You do not own this offer",
                 "Phase not found",
                 "This phase does not belong to this offer",
                 "Phase is not in progress" -> ResponseEntity.status(400).body(new ApiResponse(check));

            case "Phase completed successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}