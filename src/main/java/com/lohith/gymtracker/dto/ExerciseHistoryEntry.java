package com.lohith.gymtracker.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Lightweight entry for exercise progress charts.
 * Aggregated by session to match the chart and table structure of the frontend.
 */
public record ExerciseHistoryEntry(
        LocalDate date,
        double maxWeight,
        int maxReps,
        double totalVolume,
        List<SetEntry> sets
) {
    public record SetEntry(
            double weight,
            int reps
    ) {}
}
