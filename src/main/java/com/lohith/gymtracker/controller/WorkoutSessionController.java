package com.lohith.gymtracker.controller;

import com.lohith.gymtracker.dto.WorkoutSessionRequest;
import com.lohith.gymtracker.dto.WorkoutSessionResponse;
import com.lohith.gymtracker.security.AuthenticatedUser;
import com.lohith.gymtracker.service.WorkoutSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class WorkoutSessionController {

    private final WorkoutSessionService sessionService;

    public WorkoutSessionController(WorkoutSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<List<WorkoutSessionResponse>> list(Authentication auth) {
        return ResponseEntity.ok(sessionService.findAll(AuthenticatedUser.id(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> get(@PathVariable Long id,
                                                       Authentication auth) {
        return ResponseEntity.ok(sessionService.findById(id, AuthenticatedUser.id(auth)));
    }

    @PostMapping
    public ResponseEntity<WorkoutSessionResponse> create(
            @Valid @RequestBody WorkoutSessionRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.create(request, AuthenticatedUser.id(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkoutSessionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkoutSessionRequest request,
            Authentication auth) {
        return ResponseEntity.ok(sessionService.update(id, request, AuthenticatedUser.id(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        sessionService.delete(id, AuthenticatedUser.id(auth));
        return ResponseEntity.noContent().build();
    }
}
