package com.learning.hotelManagementSystem.service;

import com.learning.hotelManagementSystem.entity.User;
import com.learning.hotelManagementSystem.repository.UserRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void updateIsEmailVerified(long userId, boolean isEmailVerified) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(Translations.USER_DOES_NOT_EXIST));
        user.setEmailVerified(isEmailVerified);
    }
}
