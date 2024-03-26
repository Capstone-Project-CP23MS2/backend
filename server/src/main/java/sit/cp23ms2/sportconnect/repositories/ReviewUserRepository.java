package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.cp23ms2.sportconnect.entities.ReviewActivity;
import sit.cp23ms2.sportconnect.entities.ReviewUser;


public interface ReviewUserRepository extends JpaRepository<ReviewUser, Integer> {
    @Query(
            value = "SELECT * FROM \"reviewUser\" WHERE (\"userId\" = :userId OR :userId IS NULL ) ORDER BY \"reviewId\" ASC",nativeQuery = true
    )
    public Page<ReviewUser> findAllReviewUsers(Pageable pageable, Integer userId);
}
