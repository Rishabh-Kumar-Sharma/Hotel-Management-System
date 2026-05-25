package com.learning.hotelManagementSystem.security;

import com.learning.hotelManagementSystem.DTO.UserDTO.CreateUserRequest;
import com.learning.hotelManagementSystem.DTO.UserDTO.CreateUserResponse;
import com.learning.hotelManagementSystem.DTO.UserDTO.LoginUserRequest;
import com.learning.hotelManagementSystem.DTO.UserDTO.LoginUserResponse;
import com.learning.hotelManagementSystem.entity.Customer;
import com.learning.hotelManagementSystem.entity.User;
import com.learning.hotelManagementSystem.exceptions.DuplicateEntityException;
import com.learning.hotelManagementSystem.repository.CustomerRepository;
import com.learning.hotelManagementSystem.repository.UserRepository;
import com.learning.hotelManagementSystem.translations.Translations;
import com.learning.hotelManagementSystem.types.AuthProviderTypesEnum;
import com.learning.hotelManagementSystem.types.UserType;
import com.learning.hotelManagementSystem.utils.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    @Transactional
    private User signupInternal(CreateUserRequest userData, AuthProviderTypesEnum authProvider, String providerId) throws DuplicateEntityException {
        if(userRepository.existsByUserName(userData.userName())) {
            throw new DuplicateEntityException(Translations.USER_ALREADY_EXISTS);
        }

        User user=User
                .builder()
                .userName(userData.userName())
                .name(userData.name())
                .providerId(providerId)
                .userType(UserType.CUSTOMER)
                .authProviderType(authProvider)
                .roles(Set.of(UserType.CUSTOMER))  // by-default the user will be created as a customer,
                // later on they can be upgraded to any other role
                .build();

        if(authProvider==AuthProviderTypesEnum.EMAIL) {
            user.setPassword(passwordEncoder.encode(userData.password()));
        }

        userRepository.save(user);
        Customer customer=Customer
                .builder()
                .user(user)
                .isActive(true)
                .build();

        customerRepository.save(customer);

        return user;
    }
    public CreateUserResponse signup(CreateUserRequest userRequest) {
        User user=signupInternal(userRequest, AuthProviderTypesEnum.EMAIL,null);
        return new CreateUserResponse(user.getUsername(),user.getId());
    }

    public LoginUserResponse login(LoginUserRequest loginUserRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserRequest.userName(), loginUserRequest.password())
            );

            User user = (User) authentication.getPrincipal();
            String authToken = authUtil.generateToken(user);
            return new LoginUserResponse(authToken, user.getId(), user.getName(), user.getContactNo());
        } catch(BadCredentialsException e) {
            throw new BadCredentialsException(Translations.INVALID_CREDENTIALS);
        }
    }
}
