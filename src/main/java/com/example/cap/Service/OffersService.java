package com.example.cap.Service;

import com.example.cap.Model.ConstructionProject;
import com.example.cap.Model.Contractors;
import com.example.cap.Model.Offers;
import com.example.cap.Model.User;
import com.example.cap.Repository.ConstructionProjectRepository;
import com.example.cap.Repository.ContractorsRepository;
import com.example.cap.Repository.OffersRepository;
import com.example.cap.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class OffersService {
    private final OffersRepository offersRepository;
    private final ConstructionProjectRepository constructionProjectRepository;
    private final ContractorsRepository contractorsRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;


    public String add(Offers offers) {

        Contractors contractors = contractorsRepository.findContractorsById(offers.getContractorId());
        ConstructionProject constructionProject = constructionProjectRepository.findConstructionProjectById(offers.getProjectId());

        if (contractors == null) {
            return "Contractor not found";
        }

        if (constructionProject == null) {
            return "Construction project not found";
        }

        if (constructionProject.getStatus().equals("ACCEPTED")) {
            return "This project is no longer accepting offers";
        }


        long duration = ChronoUnit.DAYS.between(offers.getStartDate(), offers.getExpectedEndDate());
         if (duration <= 0) {
            return "Expected end date must be after start date";
        }



        List<Offers> checkOffers = offersRepository.findByContractorIdAndProjectId(offers.getContractorId(), offers.getProjectId());

        for (Offers o : checkOffers) {

            if (o.getStatus().equals("CANCELLED")||o.getStatus().equals("REJECTED")) {
                continue;
            }
            return "This contractor already has an active offer for this project";

        }




        constructionProject.setStatus("HAS_OFFERS");
        constructionProjectRepository.save(constructionProject);


        offers.setStatus("PENDING");
        offers.setDuration((int) duration);
        offersRepository.save(offers);

        return "Offer added successfully";
    }




    public String update(Integer contractorId,Integer offersId,Offers updateOffers){

        Offers offers=offersRepository.findOffersById(offersId);

        if(offers==null){
            return "Offer not found";
        }
        if (!offers.getContractorId().equals(contractorId)) {
            return "You do not own this offer";
        }

        if(!offers.getStatus().equals("PENDING")){
            return "Only pending offers can be updated";
        }

        long duration = ChronoUnit.DAYS.between(updateOffers.getStartDate(), updateOffers.getExpectedEndDate());
        if (duration <= 0) {
            return "Expected end date must be after start date";
        }

        offers.setDuration((int) duration);
        offers.setExpectedEndDate(updateOffers.getExpectedEndDate());
        offers.setPrice(updateOffers.getPrice());
        offers.setStartDate(updateOffers.getStartDate());
        offersRepository.save(offers);
        return "Offer update successfully";

    }

      public String delete(Integer contractorId,Integer offersId){

        Offers offers=offersRepository.findOffersById(offersId);

        if(offers==null){
            return "Offer not found";
        }

        if(!offers.getStatus().equals("PENDING")){
           return "Only pending offers can be cancelled";
        }
          if(!offers.getContractorId().equals(contractorId)) {
              return "You do not own this offer";
          }


          offers.setStatus("CANCELLED");
          offersRepository.save(offers);
          return "Offer cancelled successfully";
}


    public List<Offers> getOffersForUser(Integer userId,Integer projectId) {
        ConstructionProject project = constructionProjectRepository.findConstructionProjectById(projectId);
        if (project == null) {
            return null;
        }
        if (!project.getUserId().equals(userId)) {
            return null;
        }
        return offersRepository.findAllByProjectId(projectId);

    }


        public String acceptOffers(Integer userId,Integer offersId) {
            Offers offers = offersRepository.findOffersById(offersId);

            if (offers == null) {
                return "Offer not found";
            }


            if (!offers.getStatus().equals("PENDING")) {
                return "Only pending offers can be accepted";
            }

            // نجيب كل العروض الخاصة بنفس المشروع
            List<Offers> allOffers = offersRepository.findAllByProjectId(offers.getProjectId());

            // التحقق من ملكية المشروع واستخدام بياناته في الإيميل
            ConstructionProject project = constructionProjectRepository.findConstructionProjectById(offers.getProjectId());
            if (project == null) {
                return "Construction project not found";
            }

            if (!project.getUserId().equals(userId)) {
                return "You do not own this project";
            }
            //فقط عشان الرساله
            User user = userRepository.findUsersById(project.getUserId());

            // نقبل العرض المختار ونرفض باقي العروض
            for (Offers offer : allOffers) {

                Contractors contractor = contractorsRepository.findContractorsById(offer.getContractorId());
                if (offer.getId().equals(offersId)) {
                    offer.setStatus("ACCEPTED");

                    emailService.sendEmail(
                            contractor.getEmail(),
                            "Your Offer Has Been Accepted - BAYTI",
                            "Hello " + contractor.getName() + ",\n\n" +
                                    "Congratulations! Your offer has been accepted by " + user.getName() + ".\n\n" +
                                    "Offer Details:\n" +
                                    "Project ID: " + offer.getProjectId() + "\n" +
                                    "Price: " + offer.getPrice() + "\n" +
                                    "Start Date: " + offer.getStartDate() + "\n" +
                                    "Expected End Date: " + offer.getExpectedEndDate() + "\n" +
                                    "Duration: " + offer.getDuration() + " days\n\n" +
                                    "The project owner will contact you regarding the next steps.\n\n" +
                                    "Thank you for using BAYTI.");
                } else if(offer.getStatus().equals("PENDING")){
                    offer.setStatus("REJECTED");
                    emailService.sendEmail(contractor.getEmail(),
                            "Update on Your Offer - BAYTI",
                            "Hello " + contractor.getName() + ",\n\n" +
                                    "Thank you for submitting your offer for Project ID "
                                    + offer.getProjectId() + ".\n\n" +
                                    "Unfortunately, your offer was not selected by "
                                    + user.getName() + ".\n\n" +
                                    "Offer Details:\n" +
                                    "Price: " + offer.getPrice() + "\n" +
                                    "Start Date: " + offer.getStartDate() + "\n" +
                                    "Expected End Date: " + offer.getExpectedEndDate() + "\n" +
                                    "Duration: " + offer.getDuration() + " days\n\n" +
                                    "Thank you for your interest in BAYTI."
                    );
                }
            }
            offersRepository.saveAll(allOffers);
            project.setStatus("ACCEPTED");
            constructionProjectRepository.save(project);
            return "Offer accepted successfully";
        }



  }











