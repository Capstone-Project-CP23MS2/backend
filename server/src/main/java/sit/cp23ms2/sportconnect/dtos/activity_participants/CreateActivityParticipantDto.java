package sit.cp23ms2.sportconnect.dtos.activity_participants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.cp23ms2.sportconnect.enums.RSVPStatusParticipant;
import sit.cp23ms2.sportconnect.enums.StatusParticipant;
import sit.cp23ms2.sportconnect.utils.ValueOfEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityParticipantDto {
    private Integer userId;
    private Integer activityId;

    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = StatusParticipant.class)
    private String status;

    @Enumerated(EnumType.STRING)
    @ValueOfEnum(enumClass = RSVPStatusParticipant.class)
    private String rsvpStatus;

    private Instant joinedAt;
}
