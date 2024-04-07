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
            value = "SELECT * FROM \"activities\" WHERE (\"categoryId\" IN (:categoryIds) OR COALESCE(:categoryIds) IS NULL) " +
                    "AND (LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL)" +
                    "AND \"activityId\" = :activityId OR :activityId IS NULL ORDER BY \"activityId\"",nativeQuery = true
    )
    Page<Activity> findAllActivities(
            Pageable pageable,
            @Param("categoryIds") Set<Integer> categoryIds,
            @Param("title") String title,
            @Param("activityId") Integer activityId
    );

    @Query(
            value = "SELECT * FROM \"activities\" WHERE " +
                    "(LOWER(\"title\") LIKE LOWER(concat('%', :title, '%')) OR LOWER(:title) IS NULL)" +
                    "AND \"activityId\" = :activityId OR :activityId IS NULL ORDER BY \"activityId\"",nativeQuery = true
    )
    Page<Activity> findAllActivitiesNoCategoryFilter(
            Pageable pageable,
            @Param("title") String title,
            @Param("activityId") Integer activityId
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
