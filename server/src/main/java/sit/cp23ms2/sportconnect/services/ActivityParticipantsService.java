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
import sit.cp23ms2.sportconnect.dtos.activity_participants.UpdateActivityParticipantDto;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.enums.StatusParticipant;
import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.ActivityParticipantRepository;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;
import sit.cp23ms2.sportconnect.utils.AuthenticationUtil;


@Service
public class ActivityParticipantsService {
    @Autowired
    private ActivityParticipantRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private AuthenticationUtil authenticationUtil;

    public PageActivityParticipantDto getActivityParticipants(int pageNum, int pageSize, Integer activityId, Integer userId) {
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        if(activityId != null || userId != null) {
            if(activityId != null && userId == null) { //ถ้า activity params != null
                if(!repository.existsByActivity_ActivityId(activityId)) throw new ApiNotFoundException("Not found any participant in this activity");
            } else if(userId != null && activityId == null) { // user params != null
                if(!repository.existsByUser_UserId(userId)) throw new ApiNotFoundException("Not found any participant from this user");
            } else { // ถ้า != null ทั้งคู่
                if(!repository.existsByActivity_ActivityIdAndUser_UserId(activityId, userId))throw new ApiNotFoundException("Not found any participant " +
                        "of this user for this activity");
            }
        }
        Page<ActivityParticipant> listActivityParticipants = repository.findAllActivityParticipants(pageRequest, activityId, userId); //ได้เป็น Pageable
        Page<ActivityParticipantsDto> pageParticipantIncludeUsername = listActivityParticipants.map(activityParticipant -> { //set username in each row
            ActivityParticipantsDto dto = modelMapper.map(activityParticipant, ActivityParticipantsDto.class);
            dto.setUsername(activityParticipant.getUser().getUsername());
            return dto;
        });
        PageActivityParticipantDto pageActivityParticipantDto = modelMapper.map(pageParticipantIncludeUsername, PageActivityParticipantDto.class); //map ใส่ PageRequestDto
        return  pageActivityParticipantDto;
    }

    public ResponseEntity<?> createActivityParticipants(CreateActivityParticipantDto newParticipant, BindingResult result)
            throws MethodArgumentNotValidException, ApiNotFoundException, ForbiddenException {
        boolean isThereActivity = activityRepository.existsById(newParticipant.getActivityId());
        boolean isThereUser = userRepository.existsById(newParticipant.getUserId());
//        if(!isCurrentUserHostTheActivity(newParticipant))
//            throw new ForbiddenException("You're not allowed to create other's participant if you're not the host");
        if(!isThereActivity)
            throw new ApiNotFoundException("Activity not found!");
        if(!isThereUser)
            throw new ApiNotFoundException("User not found!");
        if(repository.existsByActivity_ActivityIdAndUser_UserId(newParticipant.getActivityId(), newParticipant.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This user has already participated in this Activity!");
        }
        ActivityParticipant activityParticipant = modelMapper.map(newParticipant, ActivityParticipant.class);
//        ActivityParticipant createdActivityParticipant = repository.insertWithEnum(newParticipant.getUserId()
//                , newParticipant.getActivityId(), newParticipant.getStatus(), Instant.now());
        //activityParticipant.setStatus(Enum.valueOf(StatusParticipant.class, newParticipant.getStatus()));
        ActivityParticipant createdActivityParticipant = repository.saveAndFlush(activityParticipant);
        return new ResponseEntity<ActivityParticipantsDto>(modelMapper.map(createdActivityParticipant, ActivityParticipantsDto.class), HttpStatus.CREATED);
    }

    public ActivityParticipantsDto update(UpdateActivityParticipantDto updateActivityParticipant, Integer activityId, Integer userId) throws ForbiddenException {
        ActivityParticipant activityParticipant = repository.findByActivityActivityIdAndUser_UserId(activityId, userId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Participating of Activity ID: " + activityId + " and User ID: " + userId + "does not exist !"));
        if(!isCurrentUserHost(activityParticipant))
            throw new ForbiddenException("You're not allowed to edit this participant");
        ActivityParticipant updated = mapActivityParticipant(activityParticipant, updateActivityParticipant);
        return modelMapper.map(repository.saveAndFlush(updated), ActivityParticipantsDto.class);
    }

    public ActivityParticipant mapActivityParticipant(ActivityParticipant existingActivityParticipant, UpdateActivityParticipantDto updateActivityParticipant) {
        if(updateActivityParticipant.getStatus() != null) {
            existingActivityParticipant.setStatus(StatusParticipant.valueOf(updateActivityParticipant.getStatus()));
        }
        return existingActivityParticipant;
    }

    public void delete(Integer activityId, Integer userId) throws ForbiddenException {
        ActivityParticipant activityParticipant = repository.findByActivityActivityIdAndUser_UserId(activityId, userId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Participating of Activity ID: " + activityId + " and User ID: " + userId + "does not exist !"));
        if(!isCurrentUserHost(activityParticipant))
            throw new ForbiddenException("You're not allowed to delete this participant");
        repository.deleteByActivity_ActivityIdAndUser_UserId(activityId, userId);
    }

    private boolean isCurrentUserHost(ActivityParticipant activityParticipant) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            boolean isCurrentAuthOwnThisParticipant = activityParticipant.getUser().getUserId() == currentAuthId;
            boolean isCurrentAuthOwnThisActivityOfParticipant = activityParticipant.getActivity().getHostUser().getUserId() == currentAuthId;
            return isCurrentAuthOwnThisParticipant || isCurrentAuthOwnThisActivityOfParticipant;
        }

        //System.out.println("Own participant: " + isCurrentAuthOwnThisParticipant);
        //System.out.println("Own of this activity: " + isCurrentAuthOwnThisActivityOfParticipant);
        //System.out.println(isCurrentAuthOwnThisParticipant || isCurrentAuthOwnThisActivityOfParticipant);
    }

    private boolean isCurrentUserHostTheActivity(CreateActivityParticipantDto createActivityParticipantDto) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            Activity activity = activityRepository.findActivityById(createActivityParticipantDto.getActivityId()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Activity ID: " + createActivityParticipantDto.getActivityId() + " Not Found"));
            return activity.getHostUser().getUserId() == currentAuthId;
        }
    }
}
