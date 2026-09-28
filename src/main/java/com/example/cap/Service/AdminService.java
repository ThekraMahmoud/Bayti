package com.example.cap.Service;

import com.example.cap.Model.Contractors;
import com.example.cap.Model.Pending.PendingContractor;
import com.example.cap.Model.User;
import com.example.cap.Repository.ContractorsRepository;
import com.example.cap.Repository.Pending.PendingContractorRepository;
import com.example.cap.Repository.UserRepository;
import lombok.AllArgsConstructor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class AdminService {


    private final UserRepository userRepository;
    private final PendingContractorRepository pendingContractorRepository;
    private final ContractorsRepository contractorsRepository;
    private final EmailService emailService;

    @PostConstruct
    public void init(){
        crateDefaultAdmin();
    }


    public void crateDefaultAdmin(){

        if(userRepository.findByEmail("admin@bayti.com")!=null){
            return;
        }
        User admin =new User();
        admin.setName("Admin");
        admin.setEmail("admin@bayti.com");
        admin.setPhone("0500000000");
        admin.setPassword("Admin@123");
        admin.setRole("ADMIN");
        userRepository.save(admin);

    }



    public boolean approveContractor(String email){

        PendingContractor pendingContractor=pendingContractorRepository.findByEmail(email);

        if(pendingContractor==null){
            return false;
        }

        Contractors contractors=new Contractors();

        contractors.setName(pendingContractor.getName());
        contractors.setPhone(pendingContractor.getPhone());
        contractors.setEmail(pendingContractor.getEmail());
        contractors.setPassword(pendingContractor.getPassword());
        contractors.setCompanyName(pendingContractor.getCompanyName());
        contractors.setExperience(pendingContractor.getExperience());
        contractors.setLocation(pendingContractor.getLocation());
        contractors.setLicenseNumber(pendingContractor.getLicenseNumber());
        contractors.setUrlPortfolio(pendingContractor.getUrlPortfolio());
        contractors.setBio(pendingContractor.getBio());

        contractors.setIsVerified(true);

        contractorsRepository.save(contractors);
        pendingContractorRepository.delete(pendingContractor);


        emailService.sendEmail(
                contractors.getEmail(),
                "BAYTI - Contractor Account Approved",
                "Dear " + contractors.getName() + ",\n\n" +
                        "We are pleased to inform you that your contractor account has been successfully reviewed and approved by the BAYTI team.\n\n" +
                        "Your account is now active, and you can access the contractor services available on the BAYTI platform.\n\n" +
                        "Thank you for choosing BAYTI. We look forward to having you as part of our platform.\n\n" +
                        "Best regards,\n" +
                        "BAYTI Team"
        );
        return true;
    }




    public boolean rejectContractor(String email){

        PendingContractor pendingContractor = pendingContractorRepository.findByEmail(email);
        if(pendingContractor == null){
            return false;
        }
        emailService.sendEmail(
                pendingContractor.getEmail(),
                "BAYTI - Contractor Account Application Update",
                "Dear " + pendingContractor.getName() + ",\n\n" +
                        "Thank you for your interest in joining BAYTI.\n\n" +
                        "We regret to inform you that your contractor account " +
                        "application could not be approved because it did not meet " +
                        "the required requirements.\n\n" +
                        "Your submitted registration data has been removed from our system.\n\n" +
                        "If you would like to join BAYTI, please submit a new registration " +
                        "and make sure that all required information meets the platform requirements.\n\n" +
                        "Thank you for your understanding.\n\n" +
                        "Best regards,\n" +
                        "BAYTI Team"
        );
        pendingContractorRepository.delete(pendingContractor);
        return true;
    }

     public List<User> getUser(){
        return userRepository.findAll();
    }

    public List<Contractors> getContractors(){
        return contractorsRepository.findAll();
    }


    public List<PendingContractor> getPendingContractors(){
        return pendingContractorRepository.findByEmailVerifiedTrue();
    }






    public String deleteUser(String email){
        User user=userRepository.findByEmail(email);
        if(user==null){
            return "User not found";
        }

        if(email.equals("admin@bayti.com")){
            return "You cannot delete the primary admin";
        }
        userRepository.delete(user);
        return "User deleted successfully";
    }



    public String addAdmin(String email){
        User admin = userRepository.findByEmail(email);

        if(admin == null){
            return "not found";
        }
        if(admin.getRole().equals("ADMIN")){
            return "already admin account";
        }

        admin.setRole("ADMIN");
        userRepository.save(admin);

        emailService.sendEmail(
                admin.getEmail(),
                "BAYTI - Your Account Has Been Promoted",
                "Dear " + admin.getName() + ",\n\n" +
                        "We are pleased to inform you that your BAYTI account " +
                        "has been promoted to an administrator account.\n\n" +
                        "You now have access to the administrative features available " +
                        "on the BAYTI platform.\n\n" +
                        "Please keep your account credentials secure and do not share " +
                        "your password with anyone.\n\n" +
                        "Best regards,\n" +
                        "BAYTI Team"
        );
        return "User promoted to admin successfully";
    }

    public boolean deleteContractors(String email){
        Contractors contractors=contractorsRepository.findByEmail(email);
        if(contractors==null){
            return false;
        }
        contractorsRepository.delete(contractors);
        return true;
    }





}
