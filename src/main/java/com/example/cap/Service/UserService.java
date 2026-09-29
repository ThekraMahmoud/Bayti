package com.example.cap.Service;


import com.example.cap.Model.ConstructionProject;
import com.example.cap.Model.Offers;
import com.example.cap.Model.Pending.EmailVerification;
import com.example.cap.Model.Pending.PendingContractor;
import com.example.cap.Model.Pending.PendingUser;
import com.example.cap.Model.User;
import com.example.cap.Repository.ConstructionProjectRepository;
import com.example.cap.Repository.ContractorsRepository;
import com.example.cap.Repository.Pending.EmailVerificationRepository;
import com.example.cap.Repository.Pending.PendingContractorRepository;
import com.example.cap.Repository.Pending.PendingUserRepository;
import com.example.cap.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

private final EmailVerificationRepository emailVerificationRepository;
private final EmailService emailService;
private final PendingUserRepository pendingUserRepository;
private final ContractorsRepository contractorsRepository;
private final PendingContractorRepository pendingContractorRepository;


    public List<User>getUser(){
    return  userRepository.findAll();
    }


     public String addUser(User user){
         PendingContractor pendingContractor=pendingContractorRepository.findByEmail(user.getEmail());

        if (userRepository.findByEmail(user.getEmail())!=null||contractorsRepository.findByEmail(user.getEmail())!=null||pendingContractor!=null &&pendingContractor.isEmailVerified()){
            return "Email already used";
        }
        if(userRepository.findByPhone(user.getPhone()).isPresent()){
            return "Phone already used";
        }
        // Generate verification code
        String code=String.valueOf(1000+new Random().nextInt(9000));

        // Save pending user
         PendingUser pendingUser = pendingUserRepository.findByEmail(user.getEmail());

         if (pendingUser == null) {
             pendingUser = new PendingUser();
         }

        pendingUser.setName(user.getName());
        pendingUser.setEmail(user.getEmail());
        pendingUser.setPhone(user.getPhone());
        pendingUser.setPassword(user.getPassword());
        pendingUserRepository.save(pendingUser);


        // Save verification code
         EmailVerification emailVerification = emailVerificationRepository.findByEmail(user.getEmail());
         if (emailVerification == null) {
             emailVerification = new EmailVerification();
         }

         emailVerification.setEmail(user.getEmail());
         emailVerification.setCode(code);
         emailVerification.setExpiresAt(
         LocalDateTime.now().plusMinutes(10)
         );

         emailVerificationRepository.save(emailVerification);
         emailVerification.setEmail(user.getEmail());
         emailVerification.setCode(code);
         emailVerification.setExpiresAt(
                //معناته خذ الوقت الحالي واضيف عليه ١٠ دقايق
                    LocalDateTime.now().plusMinutes(10));

         // Save verification email
        emailVerificationRepository.save(emailVerification);

         // Send verification email
         emailService.sendEmail(
                 user.getEmail(),
                 "BAYTI - Email Verification Code",
                 "Hello " + user.getName() + ",\n\n" +
                         "Thank you for registering with BAYTI.\n\n" +
                         "To complete your account registration, please use the verification code below:\n\n" +
                         "Verification Code: " + code + "\n\n" +
                         "This code is valid for 10 minutes. For your security, please do not share this code with anyone.\n\n" +
                         "If you did not request to create a BAYTI account, please ignore this email.\n\n" +
                         "Thank you for choosing BAYTI.\n\n" +
                         "Best regards,\n" +
                         "BAYTI Team"
         );
        return "Verification code sent to your email";

    }



      public String verifyEmail(String email, String code){

       EmailVerification emailVerification= emailVerificationRepository.findByEmailAndCode(email,code);
       if(emailVerification==null){
           return "Invalid email or verification code";
       }
       // هنا جيت اشيك ع الترايخ اذا كان بعد العشر دقايق الي انا محددته خلاص يجيه خطا
       if(emailVerification.getExpiresAt().isBefore(LocalDateTime.now())){
           return "Verification code expired";
       }

      //اخذ القيم الي موجوده في الجدول الافتراضي الفارغ واحطها في الجدول الحقيقي
      PendingUser p= pendingUserRepository.findByEmail(email);
          if (p == null) {
              return "Registration data not found";
          }


       User user=new User();
       user.setName(p.getName());
       user.setEmail(p.getEmail());
       user.setPhone(p.getPhone());
       user.setPassword(p.getPassword());
       user.setRole("USER");
       userRepository.save(user);

       //احذف البيانات الي ما احتاجها
       pendingUserRepository.delete(p);
       emailVerificationRepository.delete(emailVerification);
       return "User added successfully";
    }



    public Boolean update(Integer id ,User user){
       User up = userRepository.findUsersById(id);
       if(up ==null){
           return false;
       }
       up.setName(user.getName());
       userRepository.save(up);
       return true;
    }



    public boolean delete(Integer id){
        User  toDelete=userRepository.findUsersById(id);

        if(toDelete==null||toDelete.getRole().equals("ADMIN")){
            return false;   
        }
        userRepository.delete(toDelete);
        emailService.sendEmail(
                toDelete.getEmail(),
                "Account Deleted - BAYTI",
                "Hello " + toDelete.getName() + ",\n\n" +
                        "Your BAYTI account has been successfully deleted.\n\n" +
                        "We are sorry to see you leave, and we would appreciate knowing if there was any issue that led you to delete your account. " +
                        "Your feedback helps us improve the BAYTI experience for everyone.\n\n" +
                        "If you deleted your account by mistake or would like to restore it, please contact our technical support team, " +
                        "and we will assist you with the account recovery process.\n\n" +
                        "Thank you for being part of BAYTI.\n\n" +
                        "Best regards,\n" +
                        "BAYTI Team"
        );
        return true;
    }







}
