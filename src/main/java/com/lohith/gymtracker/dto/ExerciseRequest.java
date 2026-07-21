package com.lohith.gymtracker.dto;

import jakarta.validation.constraints.NotBlank;

public record ExerciseRequest(
        @NotBlank(message = "Exercise name is required") String name,
        @NotBlank(message = "Muscle group is required") String muscleGroup,
        String notes
) {}
