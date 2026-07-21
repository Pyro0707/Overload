package com.lohith.gymtracker.repository;

import com.lohith.gymtracker.model.RoutineExercise;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, Long> {

    @EntityGraph(attributePaths = {"exercise"})
    List<RoutineExercise> findByUserIdAndRoutineNameOrderByExerciseOrderAsc(
            Long userId, String routineName);

    @EntityGraph(attributePaths = {"exercise"})
    List<RoutineExercise> findByUserIdAndRoutineNameAndDayOfWeekOrderByExerciseOrderAsc(
            Long userId, String routineName, String dayOfWeek);

    @Query("SELECT DISTINCT re.routineName FROM RoutineExercise re WHERE re.user.id = :userId")
    List<String> findDistinctRoutineNamesByUserId(Long userId);

    @Query("SELECT DISTINCT re.dayOfWeek FROM RoutineExercise re " +
           "WHERE re.user.id = :userId AND re.routineName = :routineName")
    List<String> findDistinctDaysByUserIdAndRoutineName(Long userId, String routineName);

    void deleteByUserIdAndRoutineName(Long userId, String routineName);

    void deleteByUserIdAndRoutineNameAndDayOfWeek(Long userId, String routineName, String dayOfWeek);

    void deleteByUserIdAndRoutineNameAndDayOfWeekAndExerciseId(Long userId, String routineName, String dayOfWeek, Long exerciseId);

    @EntityGraph(attributePaths = {"exercise"})
    java.util.Optional<RoutineExercise> findByUserIdAndRoutineNameAndDayOfWeekAndExerciseId(
            Long userId, String routineName, String dayOfWeek, Long exerciseId);

    long countByUserIdAndRoutineNameAndDayOfWeek(Long userId, String routineName, String dayOfWeek);
}
