package com.lohith.gymtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "set_logs",
    indexes = @Index(name = "idx_setlog_exercise_session", columnList = "exercise_id, session_id")
)
public class SetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Integer reps;

    @Column(columnDefinition = "NUMERIC(3,1)")
    private Double rpe;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "logged_at", nullable = false, updatable = false)
    private LocalDateTime loggedAt;

    protected SetLog() {
    }

    public SetLog(WorkoutSession session, Exercise exercise, Integer setNumber,
                  Double weight, Integer reps, Double rpe, String notes) {
        this.session = session;
        this.exercise = exercise;
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.rpe = rpe;
        this.notes = notes;
    }

    @PrePersist
    private void onCreate() {
        this.loggedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ────────────────────────────────────

    public Long getId() {
        return id;
    }

    public WorkoutSession getSession() {
        return session;
    }

    public void setSession(WorkoutSession session) {
        this.session = session;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(Integer setNumber) {
        this.setNumber = setNumber;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getRpe() {
        return rpe;
    }

    public void setRpe(Double rpe) {
        this.rpe = rpe;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }
}
