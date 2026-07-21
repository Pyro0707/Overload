package com.lohith.gymtracker.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SetLogRequest(
        @NotNull(message = "Exercise ID is required") Long exerciseId,
        @Min(value = 1, message = "Set number must be at least 1") int setNumber,
        @Positive(message = "Weight must be positive") double weight,
        @Min(value = 1, message = "Reps must be at least 1") int reps,
        @DecimalMin(value = "6.0", message = "RPE must be between 6.0 and 10.0")
        @DecimalMax(value = "10.0", message = "RPE must be between 6.0 and 10.0")
        Double rpe,
        String notes
) {}

