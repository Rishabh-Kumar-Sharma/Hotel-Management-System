package com.learning.hotelManagementSystem.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PermissionTypesEnum {
    CUSTOMER_ADD("customer:add"),
    CUSTOMER_DELETE("customer:delete"),
    CUSTOMER_MODIFY("customer:update"),
    STAFF_ADD("staff:add"),
    STAFF_DELETE("staff:delete"),
    STAFF_MODIFY("staff:modify");

    private final String permission;
}
