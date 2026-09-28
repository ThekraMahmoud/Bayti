package com.example.cap.Service;


import com.example.cap.Model.*;
import com.example.cap.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MaterialsService {
    private final MaterialsRepository materialsRepository;
    private final ConstructionPhasesRepository constructionPhasesRepository;
private final ContractorsRepository contractorsRepository;
private final OffersRepository offersRepository;
private final EmailService emailService;
private final ConstructionProjectRepository constructionProjectRepository;
private final UserRepository userRepository;

    public List<Materials> get(Integer phaseId){
        return materialsRepository.findMaterialsByConstructionPhaseId(phaseId);
    }



    public String add(Integer contractorsId,Materials materials){

        Contractors contractors=contractorsRepository.findContractorsById(contractorsId);
        if(contractors==null){
            return "Contractor not found";
        }

        ConstructionPhases phases =constructionPhasesRepository.findConstructionPhasesById(materials.getConstructionPhaseId());
        if(phases==null){
            return "Construction phase not found";
        }


        Offers offers=offersRepository.findOffersById(phases.getOffersId());
        if(offers==null){
            return "Offer not found";
        }
        if(!offers.getContractorId().equals(contractors.getId())){
            return "You do not own this construction phase";
        }


        if(!phases.getStatus().equals("IN PROGRESS")){
            return "Materials can only be added while the phase is in progress";
        }
        if (materials.getPurchasedQuantity() < materials.getUsedQuantity()) {
            return "Purchased quantity cannot be less than used quantity";
        }

        materials.setSurplusQuantity(materials.getPurchasedQuantity() - materials.getUsedQuantity());
        materialsRepository.save(materials);
        return "Material added successfully";
    }



    public String increasePurchasedQuantity(Integer contractorsId , Integer materialsId ,Integer count ){

        Contractors contractors=contractorsRepository.findContractorsById(contractorsId);
        if(contractors==null){
            return "Contractor not found";
        }
        Materials materials=materialsRepository.findById(materialsId).orElse(null);
        if(materials==null){
            return "Material not found";
        }

        ConstructionPhases phases=constructionPhasesRepository.findConstructionPhasesById(materials.getConstructionPhaseId());
        if (phases == null) {
            return "Construction phase not found";
        }
// Material cannot be updated once the construction phase is completed.
        if (!phases.getStatus().equals("IN PROGRESS")) {
            return "Material can only be updated while the phase is in progress";
        }

        Offers offers=offersRepository.findOffersById(phases.getOffersId());
        if(offers==null){
            return "Offer not found";
        }
        if(!offers.getContractorId().equals(contractorsId)){
            return "You do not own this material";
        }

        if(count <= 0){
            return "Quantity must be greater than zero";
        }



        Integer oldPurchasedQuantity = materials.getPurchasedQuantity();
        materials.setPurchasedQuantity(materials.getPurchasedQuantity() + count);
        materials.setSurplusQuantity(materials.getPurchasedQuantity() - materials.getUsedQuantity());

        materialsRepository.save(materials);

        if (oldPurchasedQuantity <= materials.getPlannedQuantity()
                && materials.getPurchasedQuantity() > materials.getPlannedQuantity()) {

            ConstructionProject project = constructionProjectRepository.findConstructionProjectById(offers.getProjectId());
            User user = userRepository.findUsersById(project.getUserId());

            if (user != null) {
                String message =
                        "The planned quantity for "
                                + materials.getMaterialType()
                                + " is "
                                + materials.getPlannedQuantity()
                                + ", but the purchased quantity has reached "
                                + materials.getPurchasedQuantity()
                                + ". Please review the material quantity.";

                emailService.sendEmail(
                        user.getEmail(),
                        "Material Quantity Exceeded",
                        message
                );
            }
        }

        return "Material quantity increased successfully";
    }



    public String increaseUsedQuantity(Integer contractorsId , Integer materialsId ,Integer count){

        Contractors contractors=contractorsRepository.findContractorsById(contractorsId);
         if(contractors ==null){
             return "Contractor not found";

         }


        Materials materials=materialsRepository.findById(materialsId).orElse(null);
        if(materials ==null){
            return "Material not found";

        }


        ConstructionPhases constructionPhases=constructionPhasesRepository.findConstructionPhasesById(materials.getConstructionPhaseId());
        if(constructionPhases ==null){
            return "Construction phase not found";

        }
// Material cannot be updated once the construction phase is completed.
        if (!constructionPhases.getStatus().equals("IN PROGRESS")) {
            return "Material can only be updated while the phase is in progress";
        }

        Offers offers=offersRepository.findOffersById(constructionPhases.getOffersId());
        if(offers ==null){
            return "Offer not found";
        }

        if(!offers.getContractorId().equals(contractorsId)){
            return "You do not own this construction phase";
        }


        if(count <= 0){
            return "Quantity must be greater than zero";
        }

        if (materials.getUsedQuantity() + count > materials.getPurchasedQuantity()) {
                return "Used quantity cannot be greater than purchased quantity. Please purchase more materials first";
        }

        materials.setUsedQuantity(materials.getUsedQuantity() + count);
        materials.setSurplusQuantity(materials.getPurchasedQuantity() - materials.getUsedQuantity());

        materialsRepository.save(materials);
        return "Material quantity used successfully";

    }



}
