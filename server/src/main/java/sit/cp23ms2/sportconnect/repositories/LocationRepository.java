package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.cp23ms2.sportconnect.entities.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query(value = "SELECT * FROM \"location\" WHERE ((:lat IS NULL OR :lng IS NULL OR :radius IS NULL) OR ST_DWithin(\"point\"\\:\\:geometry, ST_MakePoint(:lat, :lng), :radius, false))"
            , nativeQuery = true)
    List<Location> findAllLocationList(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Integer radius
    );
}
