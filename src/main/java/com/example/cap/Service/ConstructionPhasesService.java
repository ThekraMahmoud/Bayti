package com.example.cap.Service;

import com.example.cap.Model.*;
import com.example.cap.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class ConstructionPhasesService {
     private final ConstructionPhasesRepository constructionPhasesRepository;
     private final OffersRepository offersRepository;

 private final EmailService emailService;
 private final UserRepository userRepository;
 private final ContractorsRepository contractorsRepository;
 private final ConstructionProjectRepository constructionProjectRepository;


//    public boolean add(Integer contractorId,Integer offerId){
//
//    }




    //اضافة مراحل البناء فقط
    public String add(ConstructionPhases phases){

        Offers offers=offersRepository.findOffersById(phases.getOffersId());
        if(offers==null){
            return "Offer not found";
        }

        if (!offers.getStatus().equals("ACCEPTED")) {
            return "Offer is not accepted";
        }

        if(constructionPhasesRepository.existsByOffersIdAndPhaseName(phases.getOffersId(),phases.getPhaseName())){
            return "Phase name already exists";
        }
        phases.setStatus("NOT DONE");
        phases.setStartDate(null);
        phases.setActualEndDate(null);

        constructionPhasesRepository.save(phases);
        return "Phase added successfully";
    }



    public String updatePhase(Integer contractorsId, Integer offerId, Integer phaseId, ConstructionPhases updatePhase) {

        Offers offers = offersRepository.findOffersById(offerId);

        if (offers == null) {
            return "Offer not found";
        }

        if (!offers.getContractorId().equals(contractorsId)) {
            return "You do not own this offer";
        }

        ConstructionPhases phases = constructionPhasesRepository.findConstructionPhasesById(phaseId);
        if (phases == null) {
            return "Phase not found";
        }

        if (!phases.getOffersId().equals(offerId)) {
            return "This phase does not belong to this offer";
        }

        if (phases.getStatus().equals("COMPLETED")) {
            return "Completed phase cannot be updated";
        }
        phases.setPhaseName(updatePhase.getPhaseName());
        phases.setExpectedEndDate(updatePhase.getExpectedEndDate());

        constructionPhasesRepository.save(phases);
        return "Phase updated successfully";
    }




    public String startPhase(Integer contractorsId, Integer offerId, Integer phaseId) {

        Offers offers = offersRepository.findOffersById(offerId);

        if (offers == null) {
            return "Offer not found";
        }

        if (!offers.getContractorId().equals(contractorsId)) {
            return "You do not own this offer";
        }

        ConstructionPhases phases = constructionPhasesRepository.findConstructionPhasesById(phaseId);

        if (phases == null) {
            return "Phase not found";
        }

        if (!phases.getOffersId().equals(offerId)) {
            return "This phase does not belong to this offer";
        }

        if (!phases.getStatus().equals("NOT DONE")) {
            return "Phase has already started or completed";
        }

        phases.setStatus("IN PROGRESS");
        phases.setStartDate(LocalDate.now());

        constructionPhasesRepository.save(phases);

        ConstructionProject project = constructionProjectRepository.findConstructionProjectById(offers.getProjectId());


        User user = userRepository.findUsersById(project.getUserId());
        emailService.sendEmail(
                user.getEmail(),
                "Your Construction Phase Has Started - BAYTI",
                   "Hello " + user.getName() + ",\n\n" +
                        "We’re pleased to inform you that the contractor has officially started the " +
                        phases.getPhaseName() + " phase of your construction project.\n\n" +
                        "Phase: " + phases.getPhaseName() + "\n" +
                        "Start Date: " + phases.getStartDate() + "\n\n" +
                        "Your project is now in progress, and you can follow its progress through BAYTI.\n\n" +
                        "We’ll notify you once this phase has been completed.\n\n" +
                        "Thank you for using BAYTI."
        );

        return "Phase started successfully";
    }





    public String completePhase(Integer contractorsId, Integer offerId, Integer phaseId) {

//لازم اتحقق من المتريال هنا انها موجوده قبل ما يسوي كوبليت
        Offers offers = offersRepository.findOffersById(offerId);
        if (offers == null) {
            return "Offer not found";
        }
        if (!offers.getContractorId().equals(contractorsId)) {
            return "You do not own this offer";
        }


        ConstructionPhases phases = constructionPhasesRepository.findConstructionPhasesById(phaseId);
        if (phases == null) {
            return "Phase not found";
        }
        if (!phases.getOffersId().equals(offerId)) {
            return "This phase does not belong to this offer";
        }
        if (!phases.getStatus().equals("IN PROGRESS")) {
            return "Phase is not in progress";
        }

        phases.setStatus("COMPLETED");
        phases.setActualEndDate(LocalDate.now());

        constructionPhasesRepository.save(phases);



        //رسائل النجاح
        ConstructionProject project = constructionProjectRepository.findConstructionProjectById(offers.getProjectId());
        User user = userRepository.findUsersById(project.getUserId());

        emailService.sendEmail(
                user.getEmail(),
                "Construction Phase Completed - BAYTI",
                "Hello " + user.getName() + ",\n\n" +
                        "Good news! The contractor has successfully completed the " +
                        phases.getPhaseName() + " phase of your construction project.\n\n" +
                        "Phase: " + phases.getPhaseName() + "\n" +
                        "Start Date: " + phases.getStartDate() + "\n" +
                        "Completion Date: " + phases.getActualEndDate() + "\n\n" +
                        "You can review the materials used and the remaining quantities through your BAYTI project.\n\n" +
                        "Thank you for using BAYTI."
        );


        Contractors contractor = contractorsRepository.findContractorsById(offers.getContractorId());
        emailService.sendEmail(
                contractor.getEmail(),
                "Phase Completed Successfully - BAYTI",
                "Hello " + contractor.getName() + ",\n\n" +
                        "You have successfully completed the " +
                        phases.getPhaseName() + " phase of the construction project.\n\n" +
                        "Phase: " + phases.getPhaseName() + "\n" +
                        "Start Date: " + phases.getStartDate() + "\n" +
                        "Completion Date: " + phases.getActualEndDate() + "\n\n" +
                        "The project owner has been notified that the phase has been completed.\n\n" +
                        "Thank you for using BAYTI."
        );

        return "Phase completed successfully";
    }











    }

