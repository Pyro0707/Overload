package com.lohith.gymtracker.dto;

public record ExerciseResponse(Long id, String name, String muscleGroup, String notes) {
    public static ExerciseResponse from(com.lohith.gymtracker.model.Exercise e) {
        return new ExerciseResponse(e.getId(), e.getName(), e.getMuscleGroup(), e.getNotes());
    }
}
