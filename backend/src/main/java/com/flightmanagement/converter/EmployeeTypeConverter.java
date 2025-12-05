package com.flightmanagement.converter;

import com.flightmanagement.enums.EmployeeType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for EmployeeType enum
 * Automatically converts between EmployeeType enum and Integer database values
 * Enables seamless ORM mapping while maintaining type safety and role integration
 */
@Converter(autoApply = true)
public class EmployeeTypeConverter implements AttributeConverter<EmployeeType, Integer> {

    /**
     * Converts EmployeeType enum to database column value
     * @param attribute the EmployeeType enum value
     * @return the integer value for database storage, or null if attribute is null
     */
    @Override
    public Integer convertToDatabaseColumn(EmployeeType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    /**
     * Converts database column value to EmployeeType enum
     * @param dbData the integer value from database
     * @return the corresponding EmployeeType enum, or null if dbData is null
     * @throws IllegalArgumentException if the database value is not recognized
     */
    @Override
    public EmployeeType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return EmployeeType.fromValue(dbData);
    }
}