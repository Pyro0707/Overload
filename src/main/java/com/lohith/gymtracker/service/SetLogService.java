package com.lohith.gymtracker.service;

import com.lohith.gymtracker.dto.ExerciseHistoryEntry;
import com.lohith.gymtracker.dto.SetLogRequest;
import com.lohith.gymtracker.dto.SetLogResponse;
import com.lohith.gymtracker.model.Exercise;
import com.lohith.gymtracker.model.SetLog;
import com.lohith.gymtracker.model.WorkoutSession;
import com.lohith.gymtracker.repository.ExerciseRepository;
import com.lohith.gymtracker.repository.SetLogRepository;
import com.lohith.gymtracker.repository.WorkoutSessionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SetLogService {

    private final SetLogRepository setLogRepository;
    private final WorkoutSessionRepository sessionRepository;
    private final ExerciseRepository exerciseRepository;
    private final PrDetectionService prDetectionService;

    public SetLogService(SetLogRepository setLogRepository,
                         WorkoutSessionRepository sessionRepository,
                         ExerciseRepository exerciseRepository,
                         PrDetectionService prDetectionService) {
        this.setLogRepository = setLogRepository;
        this.sessionRepository = sessionRepository;
        this.exerciseRepository = exerciseRepository;
        this.prDetectionService = prDetectionService;
    }

    public List<SetLogResponse> findBySession(Long sessionId, Long userId) {
        sessionRepository.getSessionOrThrow(sessionId, userId);

        return setLogRepository.findBySessionIdOrderBySetNumberAsc(sessionId).stream()
                .map(SetLogResponse::from)
                .toList();
    }

    /**
     * Logs a new set. Checks for PRs BEFORE saving so the comparison is
     * against all previous sets only. Returns PR flags in the response
     * for the live in-session notification.
     */
    public SetLogResponse create(Long sessionId, SetLogRequest request, Long userId) {
        WorkoutSession session = sessionRepository.getSessionOrThrow(sessionId, userId);
        Exercise exercise = exerciseRepository.getExerciseOrThrow(request.exerciseId(), userId);

        // Check PRs before saving
        var pr = prDetectionService.check(exercise.getId(), userId,
                request.weight(), request.reps());

        var setLog = new SetLog(session, exercise, request.setNumber(),
                request.weight(), request.reps(), request.rpe(), request.notes());

        return SetLogResponse.from(setLogRepository.save(setLog), pr.weightPr(), pr.repPr());
    }

    public SetLogResponse update(Long sessionId, Long setId,
                                  SetLogRequest request, Long userId) {
        sessionRepository.getSessionOrThrow(sessionId, userId);
        SetLog setLog = setLogRepository.getSetLogOrThrow(setId, sessionId);

        setLog.setSetNumber(request.setNumber());
        setLog.setWeight(request.weight());
        setLog.setReps(request.reps());
        setLog.setRpe(request.rpe());
        setLog.setNotes(request.notes());

        return SetLogResponse.from(setLogRepository.save(setLog));
    }

    public void delete(Long sessionId, Long setId, Long userId) {
        sessionRepository.getSessionOrThrow(sessionId, userId);
        SetLog setLog = setLogRepository.getSetLogOrThrow(setId, sessionId);
        setLogRepository.delete(setLog);
    }

    /**
     * Auto-fill previous performance: returns the set logs from the most recent
     * session where this exercise was logged. Used to pre-populate weight/reps.
     */
    public List<SetLogResponse> findPreviousSets(Long exerciseId, Long userId) {
        List<Long> sessionIds = setLogRepository.findRecentSessionIdsForExercise(
                exerciseId, userId, PageRequest.of(0, 1));

        if (sessionIds.isEmpty()) {
            return List.of();
        }

        return setLogRepository.findBySessionIdAndExerciseIdOrderBySetNumberAsc(
                        sessionIds.getFirst(), exerciseId)
                .stream()
                .map(SetLogResponse::from)
                .toList();
    }

    /**
     * Returns all set logs for an exercise across all sessions, ordered by date.
     * Used for progress charts (weight/volume over time).
     */
    public List<ExerciseHistoryEntry> findHistory(Long exerciseId, Long userId) {
        List<SetLog> logs = setLogRepository.findByExerciseAndUserOrderByDateAsc(exerciseId, userId);

        Map<Long, List<SetLog>> sessionLogsMap = new LinkedHashMap<>();
        for (SetLog log : logs) {
            sessionLogsMap.computeIfAbsent(log.getSession().getId(), k -> new ArrayList<>()).add(log);
        }

        List<ExerciseHistoryEntry> history = new ArrayList<>();
        for (List<SetLog> sessionLogs : sessionLogsMap.values()) {
            if (sessionLogs.isEmpty()) {
                continue;
            }

            WorkoutSession session = sessionLogs.get(0).getSession();

            double maxWeight = 0.0;
            int maxReps = 0;
            double totalVolume = 0.0;
            List<ExerciseHistoryEntry.SetEntry> setEntries = new ArrayList<>();
            for (SetLog sl : sessionLogs) {
                double w = sl.getWeight();
                int r = sl.getReps();
                totalVolume += w * r;
                setEntries.add(new ExerciseHistoryEntry.SetEntry(w, r));
                if (w > maxWeight) {
                    maxWeight = w;
                    maxReps = r;
                } else if (Double.compare(w, maxWeight) == 0 && r > maxReps) {
                    maxReps = r;
                }
            }

            history.add(new ExerciseHistoryEntry(
                    session.getDate(),
                    maxWeight,
                    maxReps,
                    totalVolume,
                    setEntries
            ));
        }

        return history;
    }
}
