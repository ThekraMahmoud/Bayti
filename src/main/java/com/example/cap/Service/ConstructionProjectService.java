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
public class ConstructionProjectService {

    private final ConstructionProjectRepository constructionProjectRepository;
    private final LandsRepository landsRepository;
    private final UserRepository userRepository;



   public List<ConstructionProject> getAll(){
    return constructionProjectRepository.findAll();
}


    public List<ConstructionProject>get(Integer userId){
      return constructionProjectRepository.findAllByUserId(userId);
    }


    public String add( ConstructionProject project) {
        //  هل الأرض موجودة
        Lands lands = landsRepository.findLandsById(project.getLandId());
        if (lands == null) {
            return "lands id not found";
        }
        User user=userRepository.findUsersById(project.getUserId());

        if(user==null||user.getRole().equals("ADMIN")){
            return "user not found";
        }
        //  هل الأرض ملك هذا المستخدم
        if (!lands.getUserId().equals(project.getUserId())) {
            return "You do not own this land";
        }
        //  هل الأرض لديها مشروع أصلًا
        ConstructionProject p=constructionProjectRepository.findBylandId(project.getLandId());
        if(p!=null){
           return  "This land already has a construction project";
        }
        //إنشاء المشروع
        project.setStatus("OPEN");
        constructionProjectRepository.save(project);
        return "add successfully";

    }

    public String update(Integer userId,Integer projectId,ConstructionProject updateProject){


        ConstructionProject project=constructionProjectRepository.findConstructionProjectById(projectId);

        if(project==null){
            return "Construction project not found";
        }

        if(!project.getUserId().equals(userId)){
            return "You do not own this project";
        }

        if(!project.getStatus().equals("OPEN")){
            return "Construction project cannot be updated because it already has offers or has been accepted";
        }

        project.setRoom(updateProject.getRoom());
        project.setFloors(updateProject.getFloors());
        project.setBudget(updateProject.getBudget());
        project.setDescription(updateProject.getDescription());
        project.setConstructionType(updateProject.getConstructionType());
        project.setProjectType(updateProject.getProjectType());
        constructionProjectRepository.save(project);
        return "Construction project updated successfully";

    }

    public String delete(Integer userId,Integer projectId ){
        ConstructionProject project=constructionProjectRepository.findConstructionProjectById(projectId);

        if(project==null){
            return "Construction project not found";
        }

        if(!project.getUserId().equals(userId)){
            return "You do not own this project";
        }

        if(project.getStatus().equals("ACCEPTED")) {
            return "Construction project cannot be deleted because an offer has already been accepted";
        }

            constructionProjectRepository.delete(project);
        return "Construction project Delete successfully";
    }


}