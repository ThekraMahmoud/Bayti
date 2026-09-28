package com.example.cap.Repository;

import com.example.cap.Model.SurplusOrders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurplusOrdersRepository extends JpaRepository<SurplusOrders,Integer> {
}
