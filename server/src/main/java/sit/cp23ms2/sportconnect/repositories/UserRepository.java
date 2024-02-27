package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.repository.query.Param;
import sit.cp23ms2.sportconnect.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(
            value = "SELECT * FROM \"user\" WHERE (\"email\" = :email OR :email IS NULL)",nativeQuery = true
    )
    Page<User> findAllUsers(
            Pageable pageable,
            @Param("email") String email
    );

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    @Transactional
    public void deleteByEmail(String email);

    public User findByUsername(String username);

    public Optional<User> findByEmail(String email);
}
