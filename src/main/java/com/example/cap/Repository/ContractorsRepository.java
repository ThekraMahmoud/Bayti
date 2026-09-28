package com.example.cap.Repository;

import com.example.cap.Model.Contractors;
import com.example.cap.Model.Pending.PendingContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractorsRepository extends JpaRepository<Contractors,Integer> {

    Contractors findByEmail(String email);
    Optional<Contractors>findByPhone(String phone);
    Optional<Contractors>findByLicenseNumber(Integer licenseNumber);
    Contractors findContractorsById(Integer id);

}
