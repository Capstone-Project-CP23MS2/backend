package sit.cp23ms2.sportconnect.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sit.cp23ms2.sportconnect.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query(
            value = "SELECT * FROM \"notification\" WHERE (\"targetId\" = :targetId OR :targetId IS NULL) ORDER BY \"notificationId\" ASC",nativeQuery = true
    )
    public Page<Notification> findAllNotifications(Pageable pageable, Integer targetId);
}
