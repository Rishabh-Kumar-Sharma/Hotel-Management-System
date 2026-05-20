package com.learning.hotelManagementSystem.repository;

import com.learning.hotelManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByUserName(String userName);
    boolean existsByUserName(String userName);
}
