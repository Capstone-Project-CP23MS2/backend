package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.cp23ms2.sportconnect.entities.ReviewActivity;
import sit.cp23ms2.sportconnect.entities.ReviewUser;

public interface ReviewActivityRepository extends JpaRepository<ReviewActivity, Integer> {
    @Query(
            value = "SELECT * FROM \"reviewActivity\" WHERE (\"activityId\" = :activityId OR :activityId IS NULL ) ORDER BY \"reviewId\" ASC ",nativeQuery = true
    )
    public Page<ReviewActivity> findAllReviewActivities(Pageable pageable, Integer activityId);
}
