package com.lohith.gymtracker.dto;

import java.time.LocalDateTime;

public record SetLogResponse(
        Long id,
        Long exerciseId,
        String exerciseName,
        int setNumber,
        double weight,
        int reps,
        Double rpe,
        String notes,
        LocalDateTime loggedAt,
        boolean weightPr,
        boolean repPr
) {
    public static SetLogResponse from(com.lohith.gymtracker.model.SetLog sl, boolean weightPr, boolean repPr) {
        return new SetLogResponse(
                sl.getId(),
                sl.getExercise().getId(),
                sl.getExercise().getName(),
                sl.getSetNumber(),
                sl.getWeight(),
                sl.getReps(),
                sl.getRpe(),
                sl.getNotes(),
                sl.getLoggedAt(),
                weightPr,
                repPr
        );
    }

    public static SetLogResponse from(com.lohith.gymtracker.model.SetLog sl) {
        return from(sl, false, false);
    }
}
