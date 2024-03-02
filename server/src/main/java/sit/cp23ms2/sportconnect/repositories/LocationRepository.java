package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.cp23ms2.sportconnect.entities.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
