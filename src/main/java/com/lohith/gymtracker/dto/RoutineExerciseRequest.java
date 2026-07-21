package com.lohith.gymtracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RoutineExerciseRequest(
        @NotNull(message = "Exercise ID is required") Long exerciseId,
        @Min(value = 1, message = "Target sets must be at least 1") int targetSets,
        @Min(value = 0, message = "Order must be non-negative") int exerciseOrder
) {}
