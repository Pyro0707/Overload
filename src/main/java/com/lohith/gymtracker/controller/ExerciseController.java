package com.lohith.gymtracker.controller;

import com.lohith.gymtracker.dto.ExerciseHistoryEntry;
import com.lohith.gymtracker.dto.ExerciseRequest;
import com.lohith.gymtracker.dto.ExerciseResponse;
import com.lohith.gymtracker.dto.SetLogResponse;
import com.lohith.gymtracker.security.AuthenticatedUser;
import com.lohith.gymtracker.service.ExerciseService;
import com.lohith.gymtracker.service.PrDetectionService;
import com.lohith.gymtracker.service.SetLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final SetLogService setLogService;
    private final PrDetectionService prDetectionService;

    public ExerciseController(ExerciseService exerciseService,
                              SetLogService setLogService,
                              PrDetectionService prDetectionService) {
        this.exerciseService = exerciseService;
        this.setLogService = setLogService;
        this.prDetectionService = prDetectionService;
    }

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> list(Authentication auth) {
        return ResponseEntity.ok(exerciseService.findAll(AuthenticatedUser.id(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> get(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(exerciseService.findById(id, AuthenticatedUser.id(auth)));
    }

    @PostMapping
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest request,
                                                    Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exerciseService.create(request, AuthenticatedUser.id(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody ExerciseRequest request,
                                                    Authentication auth) {
        return ResponseEntity.ok(exerciseService.update(id, request, AuthenticatedUser.id(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        exerciseService.delete(id, AuthenticatedUser.id(auth));
        return ResponseEntity.noContent().build();
    }

    /**
     * Auto-fill previous performance: returns the set logs from the most recent
     * session where this exercise was logged.
     */
    @GetMapping("/{id}/previous-sets")
    public ResponseEntity<List<SetLogResponse>> previousSets(@PathVariable Long id,
                                                              Authentication auth) {
        return ResponseEntity.ok(setLogService.findPreviousSets(id, AuthenticatedUser.id(auth)));
    }

    /**
     * Returns all set logs for an exercise across all sessions.
     * Used for progress charts and PR tracking.
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ExerciseHistoryEntry>> history(@PathVariable Long id,
                                                              Authentication auth) {
        return ResponseEntity.ok(setLogService.findHistory(id, AuthenticatedUser.id(auth)));
    }

    /**
     * Returns the best estimated 1RM (Epley formula) for an exercise.
     * Used by the live 1RM progression coach on workout.html.
     * Response: {"best1Rm": 126.6}
     */
    @GetMapping("/{id}/best-1rm")
    public ResponseEntity<Map<String, Double>> best1Rm(@PathVariable Long id,
                                                        Authentication auth) {
        double best = prDetectionService.getBestEstimated1Rm(id, AuthenticatedUser.id(auth));
        return ResponseEntity.ok(Map.of("best1Rm", Math.round(best * 10.0) / 10.0));
    }
}
