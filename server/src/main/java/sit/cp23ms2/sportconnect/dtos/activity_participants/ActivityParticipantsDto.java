package sit.cp23ms2.sportconnect.dtos.activity_participants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import sit.cp23ms2.sportconnect.enums.RSVPStatusParticipant;
import sit.cp23ms2.sportconnect.enums.StatusParticipant;
import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
import sit.cp23ms2.sportconnect.repositories.UserRepository;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipantsDto {
    private Integer userId;
    private String username;
    private Integer activityId;
    private StatusParticipant status;
    private RSVPStatusParticipant rsvpStatus;
    private Instant joinedAt;

//    username = userRepository.findById(userId).orElseThrow(() -> new ApiNotFoundException("User not found!")).getUsername();
}
