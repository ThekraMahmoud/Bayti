package com.example.cap.Service;

import com.example.cap.Model.*;
import com.example.cap.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SurplusItemsService {
    private final SurplusItemsRepository surplusItemsRepository;
    private final MaterialsRepository materialsRepository;
    private final ConstructionProjectRepository constructionProjectRepository;
    private final ConstructionPhasesRepository constructionPhasesRepository;
    private final OffersRepository offersRepository;
    private final UserRepository userRepository;


    public List<SurplusItems> getAll(){
        return surplusItemsRepository.findAll();
    }


    public String add(SurplusItems surplusItems){

        // لاتحقق من الموارد اول لاني ابغا اتحقق من حالة المرحله
        Materials materials=materialsRepository.findMaterialsById(surplusItems.getMaterialId());

        if(materials==null){
            return "Material not found";
        }

        if (surplusItemsRepository.existsByMaterialIdAndSellerId(
                surplusItems.getMaterialId(),
                surplusItems.getSellerId())) {
            return "Surplus item already exists for this material";
        }

        // التحقق من المرحلة وحالتها
        ConstructionPhases constructionPhases=constructionPhasesRepository.findConstructionPhasesById(materials.getConstructionPhaseId());

        if (constructionPhases == null) {
            return "Construction phase not found";
        }

        // لا يمكن بيع الفائض قبل انتهاء المرحلة
        if(!constructionPhases.getStatus().equals("COMPLETED")){
            return "Surplus can only be listed after the construction phase is completed";
        }

        if (materials.getSurplusQuantity() <= 0) {
            return "There is no surplus available";
        }



        // التحقق من أن الفائض تابع لمشروع موجود وأن البائع هو مالك المشروع
      Offers offers=offersRepository.findOffersById(constructionPhases.getOffersId());
        if(offers==null){
            return "Offer not found";
        }

     ConstructionProject constructionProject=constructionProjectRepository.findConstructionProjectById(offers.getProjectId());
        if (constructionProject == null) {
            return "Construction project not found";
        }

        User user=userRepository.findUsersById(surplusItems.getSellerId());
        if (user == null) {
            return "User not found";
        }

        if (!constructionProject.getUserId().equals(surplusItems.getSellerId())) {
            return "You do not own this project";
        }


        surplusItems.setStatus("AVAILABLE");
        surplusItems.setQuantity(materials.getSurplusQuantity());
        surplusItems.setMaterialType(materials.getMaterialType());
        surplusItemsRepository.save(surplusItems);
        return "Surplus item added successfully";
    }


    public Boolean update(Integer sellerId,Integer surplusItemsId,SurplusItems updatesurplusItems){

        SurplusItems surplusItems=surplusItemsRepository.findSurplusItemsByIdAndSellerId(surplusItemsId,sellerId);

        if(surplusItems==null){
            return false;
        }

        surplusItems.setDescription(updatesurplusItems.getDescription());
        surplusItems.setPrice(updatesurplusItems.getPrice());
        surplusItemsRepository.save(surplusItems);
        return true;
    }



    public String closed(Integer userId, Integer surplusItemsId) {


        SurplusItems surplusItems = surplusItemsRepository.findSurplusItemsByIdAndSellerId(surplusItemsId, userId);
        if (surplusItems == null) {
            return "Surplus item not found";
        }
        if (surplusItems.getStatus().equals("SOLD")) {
            return "Sold surplus item cannot be closed";
        }
        if (surplusItems.getStatus().equals("CLOSED")) {
            return "Surplus item is already closed";
        }


        surplusItems.setStatus("CLOSED");
        surplusItemsRepository.save(surplusItems);
        return "Surplus item closed successfully";
    }


    public String reopen(Integer userId, Integer surplusItemsId) {

        SurplusItems surplusItems = surplusItemsRepository.findSurplusItemsByIdAndSellerId(surplusItemsId, userId);

        if (surplusItems == null) {
            return "Surplus item not found";
        }

        if (surplusItems.getStatus().equals("SOLD")) {
            return "Sold surplus item cannot be reopened";
        }

        if (surplusItems.getStatus().equals("AVAILABLE")) {
            return "Surplus item is already available";
        }

        surplusItems.setStatus("AVAILABLE");
        surplusItemsRepository.save(surplusItems);

        return "Surplus item reopened successfully";
    }




    public String transfer(Integer userId,Integer surplusItemsId,Integer materialsId,Integer quantity) {
        SurplusItems surplusItems = surplusItemsRepository.findSurplusItemsByIdAndSellerId( surplusItemsId,userId);
        if (surplusItems == null) {
            return "Surplus item not found";
        }
        if (surplusItems.getStatus().equals("SOLD")) {
            return "Surplus item is not available";
        }

        // التأكد أن نوع المادة متطابق
        Materials materials = materialsRepository.findMaterialsById(materialsId);
        if (materials == null) {
            return "Material not found";
        }

        ConstructionPhases constructionPhases=constructionPhasesRepository.findConstructionPhasesById(materials.getConstructionPhaseId());
            if(constructionPhases==null){
                return "Construction phase not found";
            }

//عشان ما يقدر يرسل المواد الا لحسابه فقط
            Offers offers =offersRepository.findOffersById(constructionPhases.getOffersId());
            if(offers==null){
                return "Offer not found";
            }

            ConstructionProject constructionProject=constructionProjectRepository.findConstructionProjectById(offers.getProjectId());

            if(constructionProject==null){
                return "Construction project not found";
            }
            if (!constructionProject.getUserId().equals(userId)) {
                return "You do not own this material";
            }


        if(!materials.getMaterialType().equals(surplusItems.getMaterialType())){
            return "Material type must be the same";
        }
        if (quantity <= 0) {
            return "Quantity must be greater than zero";
        }
        if(surplusItems.getQuantity()<quantity){
            return "Quantity exceeds available surplus";
        }


        materials.setPurchasedQuantity(materials.getPurchasedQuantity()+quantity);
        materials.setSurplusQuantity(materials.getPurchasedQuantity()-materials.getUsedQuantity());
        surplusItems.setQuantity(surplusItems.getQuantity()-quantity);
        if (surplusItems.getQuantity() == 0) {
            surplusItems.setStatus("SOLD");
        }
        materialsRepository.save(materials);
        surplusItemsRepository.save(surplusItems);
        return "Surplus shared successfully";

    }
}
