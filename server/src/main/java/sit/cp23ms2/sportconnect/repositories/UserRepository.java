package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.repository.query.Param;
import sit.cp23ms2.sportconnect.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(
            value = "SELECT * FROM \"user\" WHERE (\"email\" = :email OR :email IS NULL)",nativeQuery = true
    )
    Page<User> findAllUsers(
            Pageable pageable,
            @Param("email") String email
    );

    public boolean existsByUsernameAndUserIdNot(String username, Integer id);
    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    @Transactional
    public void deleteByEmail(String email);

    public User findByUsername(String username);

    public Optional<User> findByEmail(String email);

    @Query(
            value = "SELECT u.* FROM \"user\" u JOIN \"location\" l ON u.\"locationId\" = l.\"locationId\" " +
                    "WHERE \"userId\" IN (SELECT \"userId\" FROM \"userInterest\" WHERE \"categoryId\" = :categoryId AND \"userId\" <> :hostUserId) " +
                    "AND ST_DWithin(l.\"point\"\\:\\:geometry, ST_MakePoint(:activityLng, :activityLat), 10000, false);"
            , nativeQuery = true
    )
    public Optional<List<User>> findUsersCategoriesInterests(
            @Param("categoryId") Integer categoryId,
            @Param("hostUserId") Integer hostUserId,
            @Param("activityLng") Double activityLng,
            @Param("activityLat") Double activityLat
    );

    @Query(
            value = "SELECT * FROM \"user\" u JOIN \"activityParticipants\" ap ON u.\"userId\" = ap.\"userId\" " +
                    "WHERE u.\"userId\" IN (SELECT \"userId\" FROM \"activityParticipants\" WHERE ap.\"activityId\" = :activityId AND ap.\"userId\" <> :hostUserId);"
            , nativeQuery = true
    )
    public Optional<List<User>> findUsersParticipantsInActivity(
            @Param("activityId") Integer activityId,
            @Param("hostUserId") Integer hostUserId
    );
}
