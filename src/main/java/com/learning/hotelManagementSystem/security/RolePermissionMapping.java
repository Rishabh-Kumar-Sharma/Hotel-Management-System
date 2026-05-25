package com.learning.hotelManagementSystem.security;

import com.learning.hotelManagementSystem.types.PermissionTypesEnum;
import com.learning.hotelManagementSystem.types.UserType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RolePermissionMapping {
    private static final Map<UserType, Set<PermissionTypesEnum>> mp=Map.of(
            UserType.CUSTOMER,Set.of(PermissionTypesEnum.CUSTOMER_DELETE, PermissionTypesEnum.CUSTOMER_MODIFY)
    );

    public static Set<SimpleGrantedAuthority> getPermissionsForRole(UserType userType) {
        return mp
                .get(userType)
                .stream()
                .map(permission->new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
