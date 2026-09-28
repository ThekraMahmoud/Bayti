package com.example.cap.Repository;

import com.example.cap.Model.Lands;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LandsRepository extends JpaRepository<Lands ,Integer> {

    List<Lands> findLandsByUserId(Integer id);
    Lands findLandsById(Integer id);
}
