package com.lohith.gymtracker.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WorkoutSessionRequest(
        @NotNull(message = "Date is required") LocalDate date,
        String routineName,
        String dayOfWeek,
        String notes
) {}
