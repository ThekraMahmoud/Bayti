package com.example.cap.Repository.Pending;

import com.example.cap.Model.Pending.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification,Integer> {
    EmailVerification findByEmail(String email);
    EmailVerification findByEmailAndCode(String email ,String code);
}
