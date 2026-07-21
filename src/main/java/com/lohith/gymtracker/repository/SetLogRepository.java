package com.lohith.gymtracker.repository;

import com.lohith.gymtracker.model.SetLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SetLogRepository extends JpaRepository<SetLog, Long> {

    /**
     * Returns session IDs that contain logs for a given exercise, ordered by
     * most recent first. Use with PageRequest.of(0, 1) to get just the latest.
     * Used by SetLogService.findPreviousSets() for auto-fill.
     */
    @Query("SELECT DISTINCT ws.id FROM WorkoutSession ws " +
           "JOIN SetLog sl ON sl.session = ws " +
           "WHERE sl.exercise.id = :exerciseId AND ws.user.id = :userId " +
           "ORDER BY ws.date DESC")
    List<Long> findRecentSessionIdsForExercise(Long exerciseId, Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"exercise"})
    List<SetLog> findBySessionIdOrderBySetNumberAsc(Long sessionId);

    /**
     * All set logs for a user across every session, ordered by set number.
     * Lets findAll() group in memory instead of querying once per session.
     */
    @EntityGraph(attributePaths = {"exercise"})
    @Query("SELECT sl FROM SetLog sl JOIN sl.session ws " +
           "WHERE ws.user.id = :userId ORDER BY sl.setNumber ASC")
    List<SetLog> findAllByUserId(Long userId);

    @EntityGraph(attributePaths = {"exercise"})
    List<SetLog> findBySessionIdAndExerciseIdOrderBySetNumberAsc(Long sessionId, Long exerciseId);

    /**
     * Returns the maximum weight ever logged for an exercise by a user.
     * Used for PR detection (compute-on-read).
     */
    @Query("SELECT MAX(sl.weight) FROM SetLog sl " +
           "JOIN sl.session ws " +
           "WHERE sl.exercise.id = :exerciseId AND ws.user.id = :userId")
    Optional<Double> findMaxWeightByExerciseAndUser(Long exerciseId, Long userId);

    /**
     * Returns the maximum reps ever logged at a given weight for an exercise.
     * Used for PR detection — identifies rep PRs at a specific weight.
     */
    @Query("SELECT MAX(sl.reps) FROM SetLog sl " +
           "JOIN sl.session ws " +
           "WHERE sl.exercise.id = :exerciseId AND ws.user.id = :userId " +
           "AND sl.weight = :weight")
    Optional<Integer> findMaxRepsByExerciseAndUserAndWeight(
            Long exerciseId, Long userId, Double weight);

    /**
     * Returns all set logs for a given exercise and user, grouped by session.
     * Used for progress charts (weight/volume over time).
     */
    @Query("SELECT sl FROM SetLog sl " +
           "JOIN FETCH sl.session ws " +
           "WHERE sl.exercise.id = :exerciseId AND ws.user.id = :userId " +
           "ORDER BY ws.date ASC, sl.setNumber ASC")
    List<SetLog> findByExerciseAndUserOrderByDateAsc(Long exerciseId, Long userId);

    /**
     * Returns the best estimated 1RM (Epley formula) ever logged for an exercise.
     * Used for the live 1RM progression coach on workout.html.
     * Computed entirely in the database for < 2ms latency.
     */
    @Query("SELECT MAX(sl.weight * (1.0 + sl.reps / 30.0)) FROM SetLog sl " +
           "JOIN sl.session ws " +
           "WHERE sl.exercise.id = :exerciseId AND ws.user.id = :userId")
    Optional<Double> findMaxEstimated1RmByExerciseAndUser(Long exerciseId, Long userId);

    default SetLog getSetLogOrThrow(Long setId, Long sessionId) {
        return findById(setId)
                .filter(sl -> sl.getSession().getId().equals(sessionId))
                .orElseThrow(() -> new com.lohith.gymtracker.exception.ResourceNotFoundException("Set not found"));
    }
}
