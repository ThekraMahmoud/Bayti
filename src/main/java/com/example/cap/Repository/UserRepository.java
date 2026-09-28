package com.example.cap.Repository;

import com.example.cap.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

   User findByEmail(String email);
    Optional<User>findByPhone(String phone);
    User findUsersById(Integer id);
}
