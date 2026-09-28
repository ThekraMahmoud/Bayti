package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.Offers;
import com.example.cap.Service.OffersService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offers")
@AllArgsConstructor
public class OffersController {

    private final OffersService offersService;


    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody Offers offers, Errors errors) {

        if (errors.hasErrors()) {
            String message = errors.getFieldError().getDefaultMessage();return ResponseEntity.status(400).body(new ApiResponse(message));
        }

        String check = offersService.add(offers);
        return switch (check) {
            case "Contractor not found",
                 "Construction project not found",
                 "This project is no longer accepting offers",
                 "This contractor already has an active offer for this project" -> ResponseEntity.status(400).body(new ApiResponse(check));
            case "Offer added successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));
            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @PutMapping("/update/contractorId/{contractorId}/offersId/{offersId}")
    public ResponseEntity<?> update(@PathVariable Integer contractorId, @PathVariable Integer offersId,  @RequestBody Offers updateOffers, Errors errors) {

//        if (errors.hasErrors()) {
//            String message = errors.getFieldError().getDefaultMessage();
//            return ResponseEntity.status(400).body(new ApiResponse(message));
//        }

        String check = offersService.update(contractorId, offersId, updateOffers);
        return switch (check) {
            case "Offer not found",
                 "You do not own this offer",
                 "Only pending offers can be updated" -> ResponseEntity.status(400).body(new ApiResponse(check));
            case "Offer update successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @DeleteMapping("/delete/contractorId/{contractorId}/offersId/{offersId}")
    public ResponseEntity<?> delete(@PathVariable Integer contractorId, @PathVariable Integer offersId) {

        String check = offersService.delete(contractorId, offersId);

        return switch (check) {

            case "Offer not found",
                 "You do not own this offer",
                 "Only pending offers can be cancelled" -> ResponseEntity.status(400).body(new ApiResponse(check));
            case "Offer cancelled successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }


    @GetMapping("/user/{userId}/project/{projectId}")
    public ResponseEntity<?> getOffersForUser(@PathVariable Integer userId, @PathVariable Integer projectId) {

        List<Offers> offers = offersService.getOffersForUser(userId, projectId);
        if (offers == null) {return ResponseEntity.status(400).body(new ApiResponse("You do not own this project"));}
        return ResponseEntity.status(200).body(offers);
    }


    @PutMapping("/accept/userId/{userId}/offersId/{offersId}")
    public ResponseEntity<?> acceptOffers(@PathVariable Integer userId, @PathVariable Integer offersId) {

        String check = offersService.acceptOffers(userId, offersId);

        return switch (check) {
            case "Offer not found",
                 "Construction project not found",
                 "You do not own this project",
                 "Only pending offers can be accepted" -> ResponseEntity.status(400).body(new ApiResponse(check));
            case "Offer accepted successfully" -> ResponseEntity.status(200).body(new ApiResponse(check));

            default -> ResponseEntity.status(400).body(new ApiResponse("Something went wrong"));
        };
    }
}