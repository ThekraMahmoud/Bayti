package com.example.cap.Service;

import com.example.cap.Model.ConstructionProject;
import com.example.cap.Model.Lands;
import com.example.cap.Model.User;
import com.example.cap.Repository.ConstructionProjectRepository;
import com.example.cap.Repository.LandsRepository;
import com.example.cap.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LandsService {
    private final LandsRepository landsRepository;
    private final UserRepository userRepository;

    private final ConstructionProjectRepository constructionProjectRepository;


    public List<Lands>getLandes(Integer id){
        if(userRepository.findUsersById(id)==null){
            return null;
        }
        return landsRepository.findLandsByUserId(id);
    }



    public String add(Lands lands){

        User user=userRepository.findUsersById(lands.getUserId());
        if(user==null){
            return "User not found";

        }
        if(user.getRole().equals("ADMIN")){
            return "Admin users are not allowed to add lands";
        }

        landsRepository.save(lands);
        return "Land added successfully";
    }


    public String update (Integer userId , Integer id , Lands landsUpdate){

        Lands lands=landsRepository.findById(id).orElse(null);
        if( lands==null){
            return "Land not found";
        }

        if (!lands.getUserId().equals(userId)) {
            return "You do not own this land";
        }


        ConstructionProject constructionProject=constructionProjectRepository.findBylandId(id);

        if (constructionProject!=null&&!constructionProject.getStatus().equals("OPEN")){
            return "Land cannot be updated because it is currently in use";
        }

        lands.setWidth(landsUpdate.getWidth());
        lands.setArea(landsUpdate.getArea());
        lands.setLength(landsUpdate.getLength());
        lands.setLocation(landsUpdate.getLocation());
        landsRepository.save(lands);
        return "Land updated successfully";
    }


    public String delete(Integer userId, Integer landId) {

        Lands lands=landsRepository.findById(landId).orElse(null);

        if(lands==null){
            return "Land not found";
        }

        if (!lands.getUserId().equals(userId)) {
            return "You do not own this land";
        }


        ConstructionProject constructionProject=constructionProjectRepository.findBylandId(landId);

        if(constructionProject!=null&&!constructionProject.getStatus().equals("OPEN")) {
            return "Land cannot be deleted because it is currently in use";
        }
        landsRepository.delete(lands);
        return "Land deleted successfully";
    }






}
