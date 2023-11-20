package sit.cp23ms2.sportconnect.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.dtos.activity_participants.ActivityParticipantsDto;
import sit.cp23ms2.sportconnect.dtos.activity_participants.CreateActivityParticipantDto;
import sit.cp23ms2.sportconnect.dtos.activity_participants.PageActivityParticipantDto;
import sit.cp23ms2.sportconnect.dtos.request.CreateRequestDto;
import sit.cp23ms2.sportconnect.dtos.request.PageRequestDto;
import sit.cp23ms2.sportconnect.dtos.request.RequestDto;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.entities.Request;
import sit.cp23ms2.sportconnect.enums.StatusParticipant;
import sit.cp23ms2.sportconnect.repositories.ActivityParticipantRepository;

import java.time.Instant;

@Service
public class ActivityParticipantsService {
    @Autowired
    private ActivityParticipantRepository repository;
    @Autowired
    private ModelMapper modelMapper;

    public PageActivityParticipantDto getActivityParticipants(int pageNum, int pageSize, Integer activityId, Integer userId) {
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<ActivityParticipant> listActivityParticipants = repository.findAllActivityParticipants(pageRequest, activityId, userId); //ได้เป็น Pageable ของ Request
        PageActivityParticipantDto pageActivityParticipantDto = modelMapper.map(listActivityParticipants, PageActivityParticipantDto.class); //map ใส่ PageRequestDto
        return  pageActivityParticipantDto;
    }

    public ResponseEntity<?> createActivityParticipants(CreateActivityParticipantDto newParticipant, BindingResult result) throws MethodArgumentNotValidException {
        if(repository.existsByActivity_ActivityIdAndUser_UserId(newParticipant.getActivityId(), newParticipant.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This user has already participated in this Party!");
        }
        ActivityParticipant activityParticipant = modelMapper.map(newParticipant, ActivityParticipant.class);
//        ActivityParticipant createdActivityParticipant = repository.insertWithEnum(newParticipant.getUserId()
//                , newParticipant.getActivityId(), newParticipant.getStatus(), Instant.now());
        //activityParticipant.setStatus(Enum.valueOf(StatusParticipant.class, newParticipant.getStatus()));
        ActivityParticipant createdActivityParticipant = repository.saveAndFlush(activityParticipant);
        return new ResponseEntity<ActivityParticipantsDto>(modelMapper.map(createdActivityParticipant, ActivityParticipantsDto.class), HttpStatus.CREATED);
    }

    public void delete(Integer activityId, Integer userId) {
        repository.findByActivityActivityIdAndUser_UserId(activityId, userId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Participating of Activity ID: " + activityId + " and User ID: " + userId + "does not exist !"));
        repository.deleteByActivity_ActivityIdAndUser_UserId(activityId, userId);
    }
}
