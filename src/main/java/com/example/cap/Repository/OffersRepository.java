package com.example.cap.Repository;

import com.example.cap.Model.Offers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffersRepository extends JpaRepository<Offers,Integer> {

    Offers findOffersById(Integer id);
    List<Offers> findByContractorIdAndProjectId(Integer contractorId , Integer projectId);

    @Query("""
    SELECT o
    FROM Offers o
    WHERE o.projectId IN (SELECT p.id FROM ConstructionProject p WHERE p.userId = :userId )
""")
    List<Offers> findAllOffersByUserId(@Param("userId") Integer userId);


    Offers findOffersByContractorId(Integer id);
    List<Offers> findAllByProjectId(Integer projectId);
}
