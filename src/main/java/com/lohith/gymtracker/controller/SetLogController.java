package com.lohith.gymtracker.controller;

import com.lohith.gymtracker.dto.SetLogRequest;
import com.lohith.gymtracker.dto.SetLogResponse;
import com.lohith.gymtracker.security.AuthenticatedUser;
import com.lohith.gymtracker.service.SetLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions/{sessionId}/sets")
public class SetLogController {

    private final SetLogService setLogService;

    public SetLogController(SetLogService setLogService) {
        this.setLogService = setLogService;
    }

    @GetMapping
    public ResponseEntity<List<SetLogResponse>> list(@PathVariable Long sessionId,
                                                      Authentication auth) {
        return ResponseEntity.ok(setLogService.findBySession(sessionId, AuthenticatedUser.id(auth)));
    }

    /**
     * Logs a new set. Response includes weightPr/repPr flags for the live
     * in-session PR notification.
     */
    @PostMapping
    public ResponseEntity<SetLogResponse> create(@PathVariable Long sessionId,
                                                  @Valid @RequestBody SetLogRequest request,
                                                  Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(setLogService.create(sessionId, request, AuthenticatedUser.id(auth)));
    }

    @PutMapping("/{setId}")
    public ResponseEntity<SetLogResponse> update(@PathVariable Long sessionId,
                                                  @PathVariable Long setId,
                                                  @Valid @RequestBody SetLogRequest request,
                                                  Authentication auth) {
        return ResponseEntity.ok(setLogService.update(sessionId, setId, request, AuthenticatedUser.id(auth)));
    }

    @DeleteMapping("/{setId}")
    public ResponseEntity<Void> delete(@PathVariable Long sessionId,
                                        @PathVariable Long setId,
                                        Authentication auth) {
        setLogService.delete(sessionId, setId, AuthenticatedUser.id(auth));
        return ResponseEntity.noContent().build();
    }
}
