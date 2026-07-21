package com.lohith.gymtracker.dto;

public record RoutineExerciseResponse(
        Long id,
        Long exerciseId,
        String exerciseName,
        String muscleGroup,
        int targetSets,
        int exerciseOrder
) {
    public static RoutineExerciseResponse from(com.lohith.gymtracker.model.RoutineExercise re) {
        com.lohith.gymtracker.model.Exercise ex = re.getExercise();
        if (ex == null) return null;
        return new RoutineExerciseResponse(re.getId(), ex.getId(), ex.getName(), ex.getMuscleGroup(), re.getTargetSets(), re.getExerciseOrder());
    }
}
