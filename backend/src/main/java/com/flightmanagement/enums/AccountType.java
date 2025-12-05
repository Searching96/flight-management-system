package com.flightmanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    CUSTOMER(1),
    EMPLOYEE(2);

    private final int value;

    AccountType(int value) {
        this.value = value;
    }

    // @JsonValue tells Spring: "When sending to Frontend, send this number"
    @JsonValue
    public int getValue() {
        return value;
    }

    // @JsonCreator tells Spring: "When Frontend sends a number, pick the matching Enum"
    @JsonCreator
    public static AccountType fromValue(int value) {
        for (AccountType type : values()) {
            if (type.value == value) return type;
        }
        throw new IllegalArgumentException("Unknown AccountType: " + value);
    }
}