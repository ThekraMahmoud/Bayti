package com.example.cap.Repository.Pending;

import com.example.cap.Model.Pending.PendingContractor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingContractorRepository
        extends JpaRepository<PendingContractor, Integer> {

    PendingContractor findByEmail(String email);
    PendingContractor findPendingContractorById(Integer id);
    List<PendingContractor> findByEmailVerifiedTrue();

}