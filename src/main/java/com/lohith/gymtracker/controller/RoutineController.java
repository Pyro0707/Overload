package com.lohith.gymtracker.controller;

import com.lohith.gymtracker.dto.RoutineCreateRequest;
import com.lohith.gymtracker.dto.RoutineDayRequest;
import com.lohith.gymtracker.dto.RoutineExerciseAddRequest;
import com.lohith.gymtracker.dto.RoutineExerciseResponse;
import com.lohith.gymtracker.security.AuthenticatedUser;
import com.lohith.gymtracker.service.RoutineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping
    public ResponseEntity<List<String>> listNames(Authentication auth) {
        return ResponseEntity.ok(routineService.listRoutineNames(AuthenticatedUser.id(auth)));
    }

    @GetMapping("/{name}")
    public ResponseEntity<Map<String, List<RoutineExerciseResponse>>> getRoutine(
            @PathVariable String name, Authentication auth) {
        return ResponseEntity.ok(routineService.getRoutine(name, AuthenticatedUser.id(auth)));
    }

    @GetMapping("/{name}/days/{day}")
    public ResponseEntity<List<RoutineExerciseResponse>> getDay(
            @PathVariable String name, @PathVariable String day, Authentication auth) {
        return ResponseEntity.ok(routineService.getDay(name, day, AuthenticatedUser.id(auth)));
    }

    @PostMapping
    public ResponseEntity<Void> createRoutine(@Valid @RequestBody RoutineCreateRequest request, Authentication auth) {
        routineService.createRoutineDay(request, AuthenticatedUser.id(auth));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{name}/days/{day}/exercises")
    public ResponseEntity<RoutineExerciseResponse> addExerciseToDay(
            @PathVariable String name, @PathVariable String day,
            @Valid @RequestBody RoutineExerciseAddRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routineService.addExerciseToDay(name, day, request, AuthenticatedUser.id(auth)));
    }

    @PatchMapping("/{name}/days/{day}/exercises/{exerciseId}")
    public ResponseEntity<RoutineExerciseResponse> updateExerciseInDay(
            @PathVariable String name, @PathVariable String day,
            @PathVariable Long exerciseId,
            @RequestBody Map<String, Integer> payload, Authentication auth) {
        Integer targetSets = payload.get("targetSets");
        return ResponseEntity.ok(routineService.updateExerciseInDay(name, day, exerciseId, targetSets, AuthenticatedUser.id(auth)));
    }

    @PutMapping("/{name}/days/{day}")
    public ResponseEntity<List<RoutineExerciseResponse>> setDay(
            @PathVariable String name, @PathVariable String day,
            @Valid @RequestBody RoutineDayRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routineService.setDay(name, day, request, AuthenticatedUser.id(auth)));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable String name, Authentication auth) {
        routineService.deleteRoutine(name, AuthenticatedUser.id(auth));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{name}/days/{day}")
    public ResponseEntity<Void> deleteDay(@PathVariable String name, @PathVariable String day,
                                           Authentication auth) {
        routineService.deleteDay(name, day, AuthenticatedUser.id(auth));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{name}/days/{day}/exercises/{exerciseId}")
    public ResponseEntity<Void> removeExerciseFromDay(
            @PathVariable String name, @PathVariable String day,
            @PathVariable Long exerciseId, Authentication auth) {
        routineService.removeExerciseFromDay(name, day, exerciseId, AuthenticatedUser.id(auth));
        return ResponseEntity.noContent().build();
    }
}
