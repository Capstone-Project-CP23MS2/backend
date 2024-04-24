package sit.cp23ms2.sportconnect.dtos.activity_participants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sit.cp23ms2.sportconnect.enums.StatusParticipant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivityParticipantDto {
    private String status;
    private String rsvpStatus;
}
