package com.learning.hotelManagementSystem.entity;

import com.learning.hotelManagementSystem.types.AuthProviderTypesEnum;
import com.learning.hotelManagementSystem.types.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "app_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String contactNo;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType userType;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProviderTypesEnum authProviderType;

    private Set<UserType> roles=new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt=LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return userName;
    }
}
