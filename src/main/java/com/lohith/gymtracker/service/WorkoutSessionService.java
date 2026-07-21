package com.lohith.gymtracker.service;

import com.lohith.gymtracker.dto.SetLogResponse;
import com.lohith.gymtracker.dto.WorkoutSessionRequest;
import com.lohith.gymtracker.dto.WorkoutSessionResponse;
import com.lohith.gymtracker.model.WorkoutSession;
import com.lohith.gymtracker.model.User;
import com.lohith.gymtracker.repository.SetLogRepository;
import com.lohith.gymtracker.repository.UserRepository;
import com.lohith.gymtracker.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkoutSessionService {

    private final WorkoutSessionRepository sessionRepository;
    private final SetLogRepository setLogRepository;
    private final UserRepository userRepository;

    public WorkoutSessionService(WorkoutSessionRepository sessionRepository,
                                  SetLogRepository setLogRepository,
                                  UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.setLogRepository = setLogRepository;
        this.userRepository = userRepository;
    }

    public List<WorkoutSessionResponse> findAll(Long userId) {
        Map<Long, List<SetLogResponse>> setsBySession = setLogRepository.findAllByUserId(userId).stream()
                .collect(Collectors.groupingBy(
                        sl -> sl.getSession().getId(),
                        Collectors.mapping(SetLogResponse::from, Collectors.toList())));

        return sessionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(ws -> toResponse(ws, setsBySession.getOrDefault(ws.getId(), List.of())))
                .toList();
    }

    public WorkoutSessionResponse findById(Long id, Long userId) {
        WorkoutSession session = sessionRepository.getSessionOrThrow(id, userId);
        return toResponse(session, loadSetsForSession(id));
    }

    private List<SetLogResponse> loadSetsForSession(Long sessionId) {
        return setLogRepository.findBySessionIdOrderBySetNumberAsc(sessionId)
                .stream()
                .map(SetLogResponse::from)
                .toList();
    }

    public WorkoutSessionResponse create(WorkoutSessionRequest request, Long userId) {
        User user = userRepository.getUserOrThrow(userId);

        var session = new WorkoutSession(
                user, request.date(), request.routineName(),
                request.dayOfWeek(), request.notes());

        return toResponse(sessionRepository.save(session), List.of());
    }

    public WorkoutSessionResponse update(Long id, WorkoutSessionRequest request, Long userId) {
        WorkoutSession session = sessionRepository.getSessionOrThrow(id, userId);
        session.setDate(request.date());
        session.setRoutineName(request.routineName());
        session.setDayOfWeek(request.dayOfWeek());
        session.setNotes(request.notes());
        return toResponse(sessionRepository.save(session), List.of());
    }

    public void delete(Long id, Long userId) {
        WorkoutSession session = sessionRepository.getSessionOrThrow(id, userId);
        sessionRepository.delete(session);
    }

    private WorkoutSessionResponse toResponse(WorkoutSession ws, List<SetLogResponse> sets) {
        return new WorkoutSessionResponse(
                ws.getId(), ws.getDate(), ws.getRoutineName(),
                ws.getDayOfWeek(), ws.getNotes(), ws.getCreatedAt(), sets);
    }
}
