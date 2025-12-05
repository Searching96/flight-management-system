package com.flightmanagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Employee type enumeration with role-based security integration
 * Provides type-safe employee categorization with embedded role names
 * Includes Jackson serialization support for frontend integration
 */
public enum EmployeeType {
    FLIGHT_SCHEDULING(1, "ROLE_EMPLOYEE_FLIGHT_SCHEDULING"),
    TICKETING(2, "ROLE_EMPLOYEE_TICKETING"),
    SUPPORT(3, "ROLE_EMPLOYEE_SUPPORT"),
    ACCOUNTING(4, "ROLE_EMPLOYEE_ACCOUNTING"),
    FLIGHT_OPERATIONS(5, "ROLE_EMPLOYEE_FLIGHT_OPERATIONS"),
    HUMAN_RESOURCES(6, "ROLE_EMPLOYEE_HUMAN_RESOURCES"),
    ADMINISTRATOR(7, "ROLE_EMPLOYEE_ADMINISTRATOR");

    private final int value;
    private final String roleName;

    EmployeeType(int value, String roleName) {
        this.value = value;
        this.roleName = roleName;
    }

    /**
     * Gets the database value for this employee type
     * Annotated with @JsonValue for JSON serialization
     * @return The integer value stored in database
     */
    @JsonValue
    public int getValue() {
        return value;
    }

    /**
     * Gets the Spring Security authority for this employee type
     * @return SimpleGrantedAuthority with the embedded role name
     */
    public SimpleGrantedAuthority getAuthority() {
        return new SimpleGrantedAuthority(roleName);
    }

    /**
     * Gets the role name for this employee type
     * Useful for direct role comparisons and logging
     * @return The Spring Security role name
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Creates an EmployeeType from its database value
     * Annotated with @JsonCreator for JSON deserialization
     * @param value The integer value from database or JSON
     * @return The corresponding EmployeeType enum
     * @throws IllegalArgumentException if the value is not recognized
     */
    @JsonCreator
    public static EmployeeType fromValue(int value) {
        for (EmployeeType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException(
            String.format("Unknown EmployeeType value: %d. Valid values are: %s", 
                value, getValidValues())
        );
    }

    /**
     * Gets a formatted string of all valid values for error messages
     * @return Comma-separated list of valid integer values
     */
    private static String getValidValues() {
        StringBuilder sb = new StringBuilder();
        for (EmployeeType type : values()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(type.value);
        }
        return sb.toString();
    }
}