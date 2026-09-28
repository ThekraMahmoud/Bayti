package com.example.cap.Repository;

import com.example.cap.Model.ConstructionPhases;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstructionPhasesRepository extends JpaRepository<ConstructionPhases,Integer> {


    ConstructionPhases findConstructionPhasesById(Integer id);
    boolean existsByOffersIdAndPhaseName(Integer offersId, String phaseName);

}
