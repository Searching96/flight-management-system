package com.flightmanagement.converter;

import com.flightmanagement.enums.AccountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter for AccountType enum
 * Automatically converts between AccountType enum and Integer database values
 * Enables seamless ORM mapping while maintaining type safety
 */
@Converter(autoApply = true)
public class AccountTypeConverter implements AttributeConverter<AccountType, Integer> {

    /**
     * Converts AccountType enum to database column value
     * @param attribute the AccountType enum value
     * @return the integer value for database storage, or null if attribute is null
     */
    @Override
    public Integer convertToDatabaseColumn(AccountType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    /**
     * Converts database column value to AccountType enum
     * @param dbData the integer value from database
     * @return the corresponding AccountType enum, or null if dbData is null
     * @throws IllegalArgumentException if the database value is not recognized
     */
    @Override
    public AccountType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return AccountType.fromValue(dbData);
    }
}