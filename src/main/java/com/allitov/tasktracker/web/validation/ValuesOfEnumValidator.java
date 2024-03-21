package com.allitov.tasktracker.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValuesOfEnumValidator implements ConstraintValidator<ValuesOfEnum, Object> {

    private Set<Object> acceptedValues;

    @Override
    public void initialize(ValuesOfEnum annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }

        if (object instanceof Collection<?> collection) {
            return acceptedValues.containsAll(collection);
        }

        return acceptedValues.contains(object);
    }
}
