package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.entities.UserInterest;
import sit.cp23ms2.sportconnect.entities.idclass.UserInterestId;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {

    @Query(
            value = "SELECT * FROM \"userInterest\" ",nativeQuery = true
    )
    Page<UserInterest> findAllUserInterest(
            Pageable pageable
    );

    public Optional<UserInterest>  findByUser_UserIdAndCategory_CategoryId(Integer userId, Integer categoryId);

    @Transactional
    public void deleteByUser_UserIdAndCategory_CategoryId(Integer userId, Integer categoryId);
}
