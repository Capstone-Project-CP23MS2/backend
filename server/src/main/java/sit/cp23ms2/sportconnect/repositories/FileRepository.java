package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.cp23ms2.sportconnect.entities.File;

import java.util.Set;

public interface FileRepository extends JpaRepository<File, Integer> {
    @Query(
            value = "SELECT * FROM \"file\" WHERE (\"activityId\" = :activityId OR :activityId IS NULL) ORDER BY \"createdAt\" DESC",nativeQuery = true
    )
    public Page<File> findAllFiles(Pageable pageable, Integer activityId);

    public Set<File> findAllByActivityFile_ActivityId(Integer activityId);
}
