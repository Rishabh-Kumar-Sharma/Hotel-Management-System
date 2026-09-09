package com.learning.hotelManagementSystem.repository;

import com.learning.hotelManagementSystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByUserName(String userName);
    boolean existsByUserName(String userName);
}
