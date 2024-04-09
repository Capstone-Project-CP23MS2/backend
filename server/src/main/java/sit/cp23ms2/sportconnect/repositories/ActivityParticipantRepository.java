package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.entities.idclass.ActivityParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;

public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, ActivityParticipantId> {

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO \"activityParticipants\" VALUES(?1, ?2, cast(?3 as status_participant), ?4)", nativeQuery = true
    )
    public void insertWithEnum(Integer userId, Integer activityId, String status, Instant joinedAt);

    @Query(
            value = "SELECT * FROM \"activityParticipants\" WHERE (\"activityId\" = :activityId OR :activityId IS NULL)" +
                    "AND (\"userId\" = :userId OR :userId IS NULL)" +
                    "AND (\"status\" = cast(:status as status_participant) OR :status IS NULL) ",nativeQuery = true
    )
    Page<ActivityParticipant> findAllActivityParticipants(
            Pageable pageable,
            @Param("activityId") Integer activityId,
            @Param("userId") Integer userId,
            @Param("status") String status
    );

    public boolean existsByActivity_ActivityIdAndUser_UserId(Integer activityId, Integer userId);
    public boolean existsByActivity_ActivityId(Integer activityId);
    public boolean existsByUser_UserId(Integer userId);
    public Optional<ActivityParticipant> findByActivityActivityIdAndUser_UserId(Integer activityId, Integer userId);
    @Transactional
    public void deleteByActivity_ActivityIdAndUser_UserId(Integer activityId, Integer userId);
}
