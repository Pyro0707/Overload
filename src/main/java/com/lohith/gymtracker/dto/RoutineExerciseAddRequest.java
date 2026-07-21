package com.lohith.gymtracker.dto;

import jakarta.validation.constraints.NotNull;

public record RoutineExerciseAddRequest(
        @NotNull(message = "Exercise ID is required")
        Long exerciseId,
        Integer targetSets
) {}
