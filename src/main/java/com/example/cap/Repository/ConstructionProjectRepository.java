package com.example.cap.Repository;

import com.example.cap.Model.ConstructionProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConstructionProjectRepository extends JpaRepository<ConstructionProject,Integer> {
    
   ConstructionProject findBylandId(Integer landID );
   ConstructionProject findConstructionProjectById(Integer id);
   ConstructionProject findConstructionProjectByUserId(Integer id);
   List<ConstructionProject>findAllByUserId(Integer id);

}
