package com.example.cap.Repository;

import com.example.cap.Model.Materials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialsRepository extends JpaRepository<Materials,Integer> {


   Materials findMaterialsById(Integer Id);
   List< Materials> findMaterialsByConstructionPhaseId(Integer Id);
}
