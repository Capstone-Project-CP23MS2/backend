package sit.cp23ms2.sportconnect.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    invite,
    join,
    leave,
    request,
    recommend,
    review,
    activity_start,
    activity_end
}
