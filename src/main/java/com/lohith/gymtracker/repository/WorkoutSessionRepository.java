whpackage com.lohith.gymtracker.repository;

import com.lohith.gymtracker.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    List<WorkoutSession> findByUserIdOrderByDateDesc(Long userId);

    Optional<WorkoutSession> findByIdAndUserId(Long id, Long userId);

    List<WorkoutSession> findByUserIdAndDateBetweenOrderByDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Returns distinct session dates for a user, ordered descending.
     * Used for streak calculation — consecutive days with at least one session.
     */
    @Query("SELECT DISTINCT ws.date FROM WorkoutSession ws " +
           "WHERE ws.user.id = :userId ORDER BY ws.date DESC")
    List<LocalDate> findDistinctDatesByUserIdOrderByDateDesc(Long userId);

    default WorkoutSession getSessionOrThrow(Long id, Long userId) {
        return findByIdAndUserId(id, userId)
                .orElseThrow(() -> new com.lohith.gymtracker.exception.ResourceNotFoundException("Session not found"));
    }
}
