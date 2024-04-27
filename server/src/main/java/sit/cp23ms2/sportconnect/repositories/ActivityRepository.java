package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.repository.query.Param;
import sit.cp23ms2.sportconnect.entities.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    @Query(
            value = "SELECT * FROM \"activities\" WHERE CASE WHEN :dateStatus = 'upcoming' THEN (\"dateTime\" >= now()) " +
                    "WHEN :dateStatus = 'past' THEN (\"dateTime\" < now()) " +
                    "ELSE (\"dateTime\" >= now() OR \"dateTime\" <= now()) " +
                    "END " +
                    "AND (\"categoryId\" IN (:categoryIds) OR COALESCE(:categoryIds) IS NULL) " +
                    "AND (LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL) " +
                    "AND (\"activityId\" = :activityId OR :activityId IS NULL) " +
                    "AND (\"hostUserId\" = :hostUserId OR :hostUserId IS NULL) " +
                    "AND (:userId IS NULL OR \"activityId\" IN (SELECT \"activityId\" FROM \"activityParticipants\" WHERE \"userId\" = :userId) AND " +
                    "\"hostUserId\" <> :userId) " +
                    "AND (\"dateTime\"\\:\\:text LIKE concat('%', :date, '%') OR :date IS NULL) ",nativeQuery = true
    )
    Page<Activity> findAllActivities(
            Pageable pageable,
            @Param("categoryIds") Set<Integer> categoryIds,
            @Param("title") String title,
            @Param("activityId") Integer activityId,
            @Param("hostUserId") Integer hostUserId,
            @Param("userId") Integer userId,
            @Param("dateStatus") String dateStatus,
            @Param("date") String date
    );

//    @Query(
//            value = "SELECT * FROM \"activities\" WHERE " +
//                    "(LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL) " +
//                    "AND (\"activityId\" = :activityId OR :activityId IS NULL) " +
//                    "AND (\"hostUserId\" = :hostUserId OR :hostUserId IS NULL) " +
//                    "AND (:userId IS NULL OR \"activityId\" IN (SELECT \"activityId\" FROM \"activityParticipants\" WHERE \"userId\" = :userId))" +
//                    "ORDER BY \"activityId\"",nativeQuery = true
//    )
    @Query(
            value = "SELECT * FROM \"activities\" WHERE CASE WHEN :dateStatus = 'upcoming' THEN (\"dateTime\" >= now()) " +
                    "WHEN :dateStatus = 'past' THEN (\"dateTime\" < now()) " +
                    "ELSE (\"dateTime\" >= now() OR \"dateTime\" <= now()) " +
                    "END " +
                    "AND (LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL) " +
                    "AND (\"activityId\" = :activityId OR :activityId IS NULL) " +
                    "AND (\"hostUserId\" = :hostUserId OR :hostUserId IS NULL) " +
                    "AND (:userId IS NULL OR \"activityId\" IN (SELECT \"activityId\" FROM \"activityParticipants\" WHERE \"userId\" = :userId) AND " +
                    "\"hostUserId\" <> :userId) " +
                    "AND (\"dateTime\"\\:\\:text LIKE concat('%', :date, '%') OR :date IS NULL) ",nativeQuery = true
    )
    Page<Activity> findAllActivitiesNoCategoryFilter(
            Pageable pageable,
            @Param("title") String title,
            @Param("activityId") Integer activityId,
            @Param("hostUserId") Integer hostUserId,
            @Param("userId") Integer userId,
            @Param("dateStatus") String dateStatus,
            @Param("date") String date
    );

    @Query(
            value = "SELECT * FROM \"activities\" a JOIN \"location\" l ON a.\"locationId\" = l.\"locationId\" " +
                    "WHERE CASE WHEN :dateStatus = 'upcoming' THEN (\"dateTime\" >= now()) " +
                    "WHEN :dateStatus = 'past' THEN (\"dateTime\" < now()) " +
                    "ELSE (\"dateTime\" >= now() OR \"dateTime\" <= now()) " +
                    "END " +
                    "AND (\"categoryId\" IN (:categoryIds) OR COALESCE(:categoryIds) IS NULL) " +
                    "AND (LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL) " +
                    "AND (\"activityId\" = :activityId OR :activityId IS NULL) " +
                    "AND (\"hostUserId\" = :hostUserId OR :hostUserId IS NULL) " +
                    "AND (:userId IS NULL OR \"activityId\" IN (SELECT \"activityId\" FROM \"activityParticipants\" WHERE \"userId\" = :userId) AND " +
                    "\"hostUserId\" <> :userId) " +
                    "AND (\"dateTime\"\\:\\:text LIKE concat('%', :date, '%') OR :date IS NULL) " +
                    "AND ((:lat IS NULL OR :lng IS NULL OR :radius IS NULL) OR ST_DWithin(l.\"point\"\\:\\:geometry, ST_MakePoint(:lng, :lat), :radius, false)) ",nativeQuery = true
    )
    List<Activity> findAllListActivities(
            @Param("categoryIds") Set<Integer> categoryIds,
            @Param("title") String title,
            @Param("activityId") Integer activityId,
            @Param("hostUserId") Integer hostUserId,
            @Param("userId") Integer userId,
            @Param("dateStatus") String dateStatus,
            @Param("date") String date,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Integer radius
    );

    @Query(
            value = "SELECT * FROM \"activities\" a JOIN \"location\" l ON a.\"locationId\" = l.\"locationId\" " +
                    " WHERE CASE WHEN :dateStatus = 'upcoming' THEN (\"dateTime\" >= now()) " +
                    "WHEN :dateStatus = 'past' THEN (\"dateTime\" < now()) " +
                    "ELSE (\"dateTime\" >= now() OR \"dateTime\" <= now()) " +
                    "END " +
                    "AND (LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL) " +
                    "AND (\"activityId\" = :activityId OR :activityId IS NULL) " +
                    "AND (\"hostUserId\" = :hostUserId OR :hostUserId IS NULL) " +
                    "AND (:userId IS NULL OR \"activityId\" IN (SELECT \"activityId\" FROM \"activityParticipants\" WHERE \"userId\" = :userId) AND " +
                    "\"hostUserId\" <> :userId) " +
                    "AND (\"dateTime\"\\:\\:text LIKE concat('%', :date, '%') OR :date IS NULL) " +
                    "AND ((:lat IS NULL OR :lng IS NULL OR :radius IS NULL) OR ST_DWithin(l.\"point\"\\:\\:geometry, ST_MakePoint(:lng, :lat), :radius, false)) ",nativeQuery = true
    )
    List<Activity> findAllListActivitiesNoCategoryFilter(
            @Param("title") String title,
            @Param("activityId") Integer activityId,
            @Param("hostUserId") Integer hostUserId,
            @Param("userId") Integer userId,
            @Param("dateStatus") String dateStatus,
            @Param("date") String date,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Integer radius
    );

    @Query(
            value = "SELECT * FROM \"activities\" WHERE " +
                    "(LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL)",nativeQuery = true
    )
    List<Activity> findAllActivitiesNoCategoryFilterNoPage(
            @Param("title") String title
    );

    @Query(
            value = "SELECT * FROM \"activities\" WHERE \"activityId\" = ?1 ",nativeQuery = true
    )
    Optional<Activity> findActivityById(Integer id);


    public boolean existsByTitleAndActivityIdNot(String title, Integer id);

    public boolean existsByTitle(String title);
}
