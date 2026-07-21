package com.lohith.gymtracker.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "routine_exercises",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_routine_day_exercise",
        columnNames = {"user_id", "routine_name", "day_of_week", "exercise_id"}
    )
)
public class RoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "routine_name", nullable = false, length = 100)
    private String routineName;

    @Column(name = "day_of_week", nullable = false, length = 50)
    private String dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = true)
    private Exercise exercise;

    @Column(name = "target_sets", nullable = false)
    private Integer targetSets;

    @Column(name = "exercise_order", nullable = false)
    private Integer exerciseOrder;

    protected RoutineExercise() {
    }

    public RoutineExercise(User user, String routineName, String dayOfWeek,
                           Exercise exercise, Integer targetSets, Integer exerciseOrder) {
        this.user = user;
        this.routineName = routineName;
        this.dayOfWeek = dayOfWeek;
        this.exercise = exercise;
        this.targetSets = targetSets;
        this.exerciseOrder = exerciseOrder;
    }

    // ── Getters & Setters ────────────────────────────────────

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Integer getTargetSets() {
        return targetSets;
    }

    public void setTargetSets(Integer targetSets) {
        this.targetSets = targetSets;
    }

    public Integer getExerciseOrder() {
        return exerciseOrder;
    }

    public void setExerciseOrder(Integer exerciseOrder) {
        this.exerciseOrder = exerciseOrder;
    }
}
