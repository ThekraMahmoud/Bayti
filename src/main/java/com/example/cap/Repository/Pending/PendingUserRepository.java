package com.example.cap.Repository.Pending;

import com.example.cap.Model.Pending.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingUserRepository
        extends JpaRepository<PendingUser, Integer> {

    PendingUser findByEmail(String email);
}