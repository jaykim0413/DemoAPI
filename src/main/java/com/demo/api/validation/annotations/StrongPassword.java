package com.demo.api.validation.annotations;

import java.lang.annotation.*;

import com.demo.api.validation.validatorClasses.StrongPasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrongPassword {
    String message() default "Must be 8 characters long and contain at least one capital letter, lowercase letter, number, and special character each.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
