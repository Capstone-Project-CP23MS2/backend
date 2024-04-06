package sit.cp23ms2.sportconnect.dtos.notification;

import lombok.Getter;
import lombok.Setter;
import sit.cp23ms2.sportconnect.enums.NotificationType;

import java.time.Instant;

@Getter
@Setter
public class NotificationDto {
    private Integer notificationId;
    private Integer targetId;
    private Integer activityId;
    private Boolean unRead;
    private NotificationType type;
    private String message;
    private Instant createdAt;
}
