package com.example.cap.Controller;

import com.example.cap.Api.ApiResponse;
import com.example.cap.Model.User;
import com.example.cap.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<?>get(){
       return ResponseEntity.status(200).body(userService.getUser());
    }

    @PostMapping("/add")
    public ResponseEntity<?>add(@RequestBody @Valid User user, Errors errors){
        if(errors.hasErrors()){
            String message=errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(400).body(message);
        }
        String check=userService.addUser(user);
        return switch (check){
            case "Email already used"->ResponseEntity.status(400).body(new ApiResponse("Email already used"));
            case "Phone already used"->ResponseEntity.status(400).body(new ApiResponse("Phone already used"));
            case "Verification code sent to your email"->ResponseEntity.status(200).body(new ApiResponse("Verification code sent to your email"));
            default -> ResponseEntity.status(400).body(new ApiResponse("somethings errors"));
        };
    }


    @PutMapping("/verifyEmail/{email}/{code}")
    public ResponseEntity<?>verifyEmail(@PathVariable String email ,@PathVariable String code){
        String check=userService.verifyEmail(email,code);
        return switch (check){
            case "Invalid email or verification code"->ResponseEntity.status(400).body(new ApiResponse("Invalid email or verification code"));
            case "Verification code expired"->ResponseEntity.status(400).body(new ApiResponse("Verification code expired"));
            case "User added successfully"->ResponseEntity.status(400).body(new ApiResponse("User added successfully"));
            default -> ResponseEntity.status(400).body(new ApiResponse("somethings errors"));

        };
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?>update(@PathVariable Integer id ,@RequestBody  User user,Errors errors){
//        if(errors.hasErrors()){
//            String message=errors.getFieldError().getDefaultMessage();
//            return ResponseEntity.status(400).body(message);
//        }
        boolean check = userService.update(id, user);
        if (!check) {
            return ResponseEntity.status(400).body(new ApiResponse("User not found"));
        }
        return ResponseEntity.status(200).body(new ApiResponse("User updated successfully"));
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?>delete(@PathVariable Integer id) {
        if (userService.delete(id)) {
            return ResponseEntity.status(200).body(new ApiResponse("delete successful"));
        }
        return ResponseEntity.status(400).body(new ApiResponse("id not found "));
    }
}
