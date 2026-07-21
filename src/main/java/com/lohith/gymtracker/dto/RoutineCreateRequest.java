package com.lohith.gymtracker.dto;

import jakarta.validation.constraints.NotBlank;

public record RoutineCreateRequest(
        @NotBlank(message = "Routine name is required")
        String routineName,
        String dayName
) {}
