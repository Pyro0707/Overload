package com.lohith.gymtracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record WorkoutSessionResponse(
        Long id,
        LocalDate date,
        String routineName,
        String dayOfWeek,
        String notes,
        LocalDateTime createdAt,
        List<SetLogResponse> sets
) {}
