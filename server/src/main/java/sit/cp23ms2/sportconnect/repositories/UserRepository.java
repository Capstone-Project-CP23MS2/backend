package sit.cp23ms2.sportconnect.repositories;

import sit.cp23ms2.sportconnect.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(
            value = "SELECT * FROM \"user\" ",nativeQuery = true
    )
    Page<User> findAllUsers(
            Pageable pageable
    );

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    public User findByUsername(String username);

    public User findByEmail(String email);
}
