package com.lohith.gymtracker.service;

import com.lohith.gymtracker.repository.SetLogRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Shared PR detection logic — used by both the live in-session toast
 * (SetLogService on POST) and the Progress screen's PR list.
 * <p>
 * A weight PR means this is the heaviest weight ever logged for this exercise.
 * A rep PR means this is the most reps ever logged at this exact weight.
 * Both are computed on-read from set_logs — no denormalized PR table.
 */
@Service
public class PrDetectionService {

    private final SetLogRepository setLogRepository;

    public PrDetectionService(SetLogRepository setLogRepository) {
        this.setLogRepository = setLogRepository;
    }

    public record PrResult(boolean weightPr, boolean repPr) {}

    /**
     * Checks whether the given weight/reps constitute a PR for this exercise.
     * Must be called BEFORE saving the new set log, so the query only
     * considers previous sets.
     */
    public PrResult check(Long exerciseId, Long userId, double weight, int reps) {
        Optional<Double> maxWeight = setLogRepository.findMaxWeightByExerciseAndUser(exerciseId, userId);
        boolean isWeightPr = maxWeight.isEmpty() || weight > maxWeight.get();

        Optional<Integer> maxReps = setLogRepository.findMaxRepsByExerciseAndUserAndWeight(
                exerciseId, userId, weight);
        boolean isRepPr = maxReps.isEmpty() || reps > maxReps.get();

        return new PrResult(isWeightPr, isRepPr);
    }

    /**
     * Returns the best estimated 1RM (Epley formula) ever logged for an exercise.
     * Returns 0.0 if no sets have been logged yet.
     * Used by the live 1RM progression coach on workout.html.
     */
    public double getBestEstimated1Rm(Long exerciseId, Long userId) {
        return setLogRepository.findMaxEstimated1RmByExerciseAndUser(exerciseId, userId)
                .orElse(0.0);
    }
}
