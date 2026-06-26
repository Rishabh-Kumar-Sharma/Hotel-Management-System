package com.learning.hotelManagementSystem.entity;

import com.learning.hotelManagementSystem.security.RolePermissionMapping;
import com.learning.hotelManagementSystem.types.AuthProviderTypesEnum;
import com.learning.hotelManagementSystem.types.UserType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
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

    @Column(unique = true, nullable = false)
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
    private Instant createdAt;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthProviderTypesEnum authProviderType;

    @PrePersist
    protected void onCreate() {
        this.createdAt= Instant.now();
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserType> roles=new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities=new HashSet<>();
        roles.forEach(role->{
            Set<SimpleGrantedAuthority> permissions= RolePermissionMapping.getPermissionsForRole(role);
            authorities.addAll(permissions);
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name()));
        });

        return authorities;
    }
}
