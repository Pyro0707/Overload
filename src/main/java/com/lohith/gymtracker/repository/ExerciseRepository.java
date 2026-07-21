package com.lohith.gymtracker.repository;

import com.lohith.gymtracker.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByUserIdOrderByNameAsc(Long userId);

    Optional<Exercise> findByIdAndUserId(Long id, Long userId);

    List<Exercise> findByUserIdAndMuscleGroupOrderByNameAsc(Long userId, String muscleGroup);

    default Exercise getExerciseOrThrow(Long id, Long userId) {
        return findByIdAndUserId(id, userId)
                .orElseThrow(() -> new com.lohith.gymtracker.exception.ResourceNotFoundException("Exercise not found: " + id));
    }
}
