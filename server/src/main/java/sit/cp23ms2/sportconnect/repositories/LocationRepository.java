package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sit.cp23ms2.sportconnect.entities.Location;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query(value = "SELECT * FROM \"location\" WHERE ((:lat IS NULL OR :lng IS NULL OR :radius IS NULL) OR ST_DWithin(\"point\"\\:\\:geometry, ST_MakePoint(:lat, :lng), :radius, false))"
            , nativeQuery = true)
    List<Location> findAllLocationList(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Integer radius
    );

//    @Modifying
//    @Transactional
    @Query(
            value = "INSERT INTO \"location\" VALUES(nextval('locations_sequence'), ?1, ?2, ?3, point(?2,?3)) RETURNING \"locationId\"", nativeQuery = true
    )
//    @Query(
//            value = "INSERT INTO \"location\" (name, latitude, longitude) VALUES (?1, ?2, ?3, point(?2, ?3)) RETURNING \"locationId\"",
//            nativeQuery = true
//    )
    public Integer insertWithEnum(
            @Param("name") String name,
            @Param("latitude") Double latitude,
            @Param("longitude")Double longitude);


}
