package com.lohith.gymtracker.service;

import com.lohith.gymtracker.dto.ExerciseRequest;
import com.lohith.gymtracker.dto.ExerciseResponse;
import com.lohith.gymtracker.model.Exercise;
import com.lohith.gymtracker.model.User;
import com.lohith.gymtracker.repository.ExerciseRepository;
import com.lohith.gymtracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public ExerciseService(ExerciseRepository exerciseRepository, UserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    public List<ExerciseResponse> findAll(Long userId) {
        List<Exercise> exercises = exerciseRepository.findByUserIdOrderByNameAsc(userId);
        if (exercises.isEmpty()) {
            exercises = seedDefaultExercises(userId);
        }
        return exercises.stream()
                .map(ExerciseResponse::from)
                .toList();
    }

    public ExerciseResponse findById(Long id, Long userId) {
        return ExerciseResponse.from(exerciseRepository.getExerciseOrThrow(id, userId));
    }

    public ExerciseResponse create(ExerciseRequest request, Long userId) {
        User user = userRepository.getUserOrThrow(userId);
        var exercise = new Exercise(request.name(), request.muscleGroup(), request.notes(), user);
        return ExerciseResponse.from(exerciseRepository.save(exercise));
    }

    public ExerciseResponse update(Long id, ExerciseRequest request, Long userId) {
        Exercise exercise = exerciseRepository.getExerciseOrThrow(id, userId);
        exercise.setName(request.name());
        exercise.setMuscleGroup(request.muscleGroup());
        exercise.setNotes(request.notes());
        return ExerciseResponse.from(exerciseRepository.save(exercise));
    }

    public void delete(Long id, Long userId) {
        Exercise exercise = exerciseRepository.getExerciseOrThrow(id, userId);
        exerciseRepository.delete(exercise);
    }

    private List<Exercise> seedDefaultExercises(Long userId) {
        User user = userRepository.getUserOrThrow(userId);
        List<Exercise> defaults = List.of(
                new Exercise("Bench Press", "Chest", "Standard barbell flat bench press", user),
                new Exercise("Incline Dumbbell Press", "Chest", "Upper chest emphasis", user),
                new Exercise("Cable Flyes", "Chest", "Chest isolation and squeeze", user),
                new Exercise("Overhead Barbell Press", "Shoulders", "Strict military press", user),
                new Exercise("Lateral Raises", "Shoulders", "Dumbbell or cable side delts", user),
                new Exercise("Face Pulls", "Shoulders", "Rear delts and rotator cuff", user),
                new Exercise("Barbell Row", "Back", "Bent over barbell row for thickness", user),
                new Exercise("Lat Pulldown", "Back", "Wide grip for back width", user),
                new Exercise("Seated Cable Row", "Back", "Mid-back and rhomboids focus", user),
                new Exercise("Barbell Bicep Curl", "Biceps", "Strict straight or EZ bar curl", user),
                new Exercise("Hammer Curls", "Biceps", "Brachialis and forearm development", user),
                new Exercise("Tricep Rope Pushdown", "Triceps", "Cable pushdown focusing on contraction", user),
                new Exercise("Overhead Tricep Extension", "Triceps", "Long head tricep emphasis", user),
                new Exercise("Barbell Back Squat", "Legs", "High or low bar compound squat", user),
                new Exercise("Romanian Deadlift", "Legs", "Hamstrings and glute hinge movement", user),
                new Exercise("Leg Press", "Legs", "Heavy machine leg press", user),
                new Exercise("Standing Calf Raises", "Legs", "Full stretch and contraction for calves", user)
        );
        return exerciseRepository.saveAll(defaults);
    }
}
