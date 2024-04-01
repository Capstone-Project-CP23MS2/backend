package sit.cp23ms2.sportconnect.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.cp23ms2.sportconnect.enums.NotificationType;
import sit.cp23ms2.sportconnect.utils.ValueOfEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDto {

    private Integer notificationId;

    private Integer targetId;


    private Boolean unRead;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = NotificationType.class)
    private String type;

    @Size(max = 50, message = "size must not over 50")
    private String message;
}
