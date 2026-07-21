package com.lohith.gymtracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RoutineDayRequest(
        @NotEmpty(message = "At least one exercise is required")
        List<@Valid RoutineExerciseRequest> exercises
) {}
