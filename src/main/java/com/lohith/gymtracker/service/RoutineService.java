package com.lohith.gymtracker.service;

import com.lohith.gymtracker.dto.RoutineCreateRequest;
import com.lohith.gymtracker.dto.RoutineDayRequest;
import com.lohith.gymtracker.dto.RoutineExerciseAddRequest;
import com.lohith.gymtracker.dto.RoutineExerciseResponse;
import com.lohith.gymtracker.exception.ResourceNotFoundException;
import com.lohith.gymtracker.model.Exercise;
import com.lohith.gymtracker.model.RoutineExercise;
import com.lohith.gymtracker.model.User;
import com.lohith.gymtracker.repository.ExerciseRepository;
import com.lohith.gymtracker.repository.RoutineExerciseRepository;
import com.lohith.gymtracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RoutineService {

    private final RoutineExerciseRepository routineExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public RoutineService(RoutineExerciseRepository routineExerciseRepository,
                          ExerciseRepository exerciseRepository,
                          UserRepository userRepository) {
        this.routineExerciseRepository = routineExerciseRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    public List<String> listRoutineNames(Long userId) {
        return routineExerciseRepository.findDistinctRoutineNamesByUserId(userId);
    }

    public Map<String, List<RoutineExerciseResponse>> getRoutine(String routineName, Long userId) {
        List<RoutineExercise> entries = routineExerciseRepository
                .findByUserIdAndRoutineNameOrderByExerciseOrderAsc(userId, routineName);

        if (entries.isEmpty()) {
            throw new ResourceNotFoundException("Routine not found: " + routineName);
        }

        Map<String, List<RoutineExerciseResponse>> grouped = new LinkedHashMap<>();
        for (RoutineExercise re : entries) {
            List<RoutineExerciseResponse> list = grouped.computeIfAbsent(re.getDayOfWeek(), k -> new java.util.ArrayList<>());
            RoutineExerciseResponse resp = RoutineExerciseResponse.from(re);
            if (resp != null) {
                list.add(resp);
            }
        }
        return grouped;
    }

    public List<RoutineExerciseResponse> getDay(String routineName, String dayOfWeek, Long userId) {
        List<RoutineExercise> entries = routineExerciseRepository
                .findByUserIdAndRoutineNameAndDayOfWeekOrderByExerciseOrderAsc(
                        userId, routineName, dayOfWeek);

        if (entries.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Day not found: " + routineName + " / " + dayOfWeek);
        }

        return entries.stream().map(RoutineExerciseResponse::from).filter(Objects::nonNull).toList();
    }

    @Transactional
    public void createRoutineDay(RoutineCreateRequest request, Long userId) {
        String routineName = request.routineName().trim();
        String dayOfWeek = (request.dayName() == null || request.dayName().trim().isEmpty())
                ? "Day 1" : request.dayName().trim();

        User user = userRepository.getUserOrThrow(userId);

        long count = routineExerciseRepository.countByUserIdAndRoutineNameAndDayOfWeek(userId, routineName, dayOfWeek);
        if (count == 0) {
            routineExerciseRepository.save(new RoutineExercise(user, routineName, dayOfWeek, null, 0, 0));
        }
    }

    @Transactional
    public RoutineExerciseResponse addExerciseToDay(String routineName, String dayOfWeek,
                                                    RoutineExerciseAddRequest request, Long userId) {
        User user = userRepository.getUserOrThrow(userId);
        Exercise exercise = exerciseRepository.getExerciseOrThrow(request.exerciseId(), userId);

        List<RoutineExercise> existing = routineExerciseRepository
                .findByUserIdAndRoutineNameAndDayOfWeekOrderByExerciseOrderAsc(userId, routineName, dayOfWeek);
        existing.stream().filter(re -> re.getExercise() == null).forEach(routineExerciseRepository::delete);

        int maxOrder = existing.stream()
                .filter(re -> re.getExercise() != null && re.getExerciseOrder() != null)
                .mapToInt(RoutineExercise::getExerciseOrder)
                .max()
                .orElse(0);

        int targetSets = request.targetSets() != null ? request.targetSets() : 4;
        RoutineExercise re = new RoutineExercise(user, routineName, dayOfWeek, exercise, targetSets, maxOrder + 1);
        re = routineExerciseRepository.save(re);
        return RoutineExerciseResponse.from(re);
    }

    @Transactional
    public void removeExerciseFromDay(String routineName, String dayOfWeek, Long exerciseId, Long userId) {
        routineExerciseRepository.deleteByUserIdAndRoutineNameAndDayOfWeekAndExerciseId(
                userId, routineName, dayOfWeek, exerciseId);

        long remaining = routineExerciseRepository.countByUserIdAndRoutineNameAndDayOfWeek(userId, routineName, dayOfWeek);
        if (remaining == 0) {
            User user = userRepository.getUserOrThrow(userId);
            routineExerciseRepository.save(new RoutineExercise(user, routineName, dayOfWeek, null, 0, 0));
        }
    }

    @Transactional
    public List<RoutineExerciseResponse> setDay(String routineName, String dayOfWeek,
                                                 RoutineDayRequest request, Long userId) {
        User user = userRepository.getUserOrThrow(userId);

        routineExerciseRepository.deleteByUserIdAndRoutineNameAndDayOfWeek(
                userId, routineName, dayOfWeek);

        if (request.exercises() == null || request.exercises().isEmpty()) {
            routineExerciseRepository.save(new RoutineExercise(user, routineName, dayOfWeek, null, 0, 0));
            return List.of();
        }

        List<RoutineExercise> saved = request.exercises().stream()
                .map(req -> {
                    Exercise exercise = exerciseRepository.getExerciseOrThrow(req.exerciseId(), userId);
                    return new RoutineExercise(user, routineName, dayOfWeek,
                            exercise, req.targetSets(), req.exerciseOrder());
                })
                .map(routineExerciseRepository::save)
                .toList();

        return saved.stream().map(RoutineExerciseResponse::from).filter(Objects::nonNull).toList();
    }

    @Transactional
    public RoutineExerciseResponse updateExerciseInDay(String routineName, String dayOfWeek, Long exerciseId,
                                                       Integer targetSets, Long userId) {
        RoutineExercise re = routineExerciseRepository
                .findByUserIdAndRoutineNameAndDayOfWeekAndExerciseId(userId, routineName, dayOfWeek, exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found in day: " + exerciseId));
        if (targetSets != null && targetSets > 0) {
            re.setTargetSets(targetSets);
            re = routineExerciseRepository.save(re);
        }
        return RoutineExerciseResponse.from(re);
    }

    @Transactional
    public void deleteRoutine(String routineName, Long userId) {
        routineExerciseRepository.deleteByUserIdAndRoutineName(userId, routineName);
    }

    @Transactional
    public void deleteDay(String routineName, String dayOfWeek, Long userId) {
        routineExerciseRepository.deleteByUserIdAndRoutineNameAndDayOfWeek(
                userId, routineName, dayOfWeek);
    }
}
