package com.learning.hotelManagementSystem.security;

import com.learning.hotelManagementSystem.repository.UserRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findUserByUserName(username).orElseThrow(()->new EntityNotFoundException(Translations.USER_DOES_NOT_EXIST));
    }
}
