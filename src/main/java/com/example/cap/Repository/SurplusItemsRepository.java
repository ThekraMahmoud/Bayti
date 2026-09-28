package com.example.cap.Repository;

import com.example.cap.Model.SurplusItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurplusItemsRepository extends JpaRepository<SurplusItems,Integer> {

    List<SurplusItems> findAllByStatus(String status);

    List<SurplusItems> findAllBySellerId(Integer sellerId);

    boolean existsByMaterialIdAndSellerId(Integer materialId, Integer sellerId);

    SurplusItems findSurplusItemsByIdAndSellerId(Integer id,Integer sellerId);

}
