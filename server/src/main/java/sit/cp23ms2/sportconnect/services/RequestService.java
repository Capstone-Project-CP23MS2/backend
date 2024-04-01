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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.dtos.request.CreateRequestDto;
import sit.cp23ms2.sportconnect.dtos.request.PageRequestDto;
import sit.cp23ms2.sportconnect.dtos.request.RequestDto;
import sit.cp23ms2.sportconnect.dtos.request.UpdateRequestDto;
import sit.cp23ms2.sportconnect.entities.Request;
import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.ActivityParticipantRepository;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.RequestRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;
import sit.cp23ms2.sportconnect.utils.AuthenticationUtil;


@Service
public class RequestService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RequestRepository repository;
    @Autowired
    ActivityParticipantRepository participantRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private AuthenticationUtil authenticationUtil;

    final private FieldError messageSizeErrorObj = new FieldError("updateRequestDto",
            "message", "Message size must not over 100 characters");

    public PageRequestDto getRequest(int pageNum, int pageSize, Integer activityId, Integer userId) {
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        if(activityId != null || userId != null) {
            if(activityId != null && userId == null) { //ถ้า activity params != null
                if(!repository.existsByActivity_ActivityId(activityId)) throw new ApiNotFoundException("Not found any request in this activity");
            } else if(userId != null && activityId == null) { // user params != null
                if(!repository.existsByUser_UserId(userId)) throw new ApiNotFoundException("Not found any request from this user");
            } else { // ถ้า != null ทั้งคู่
                if(!repository.existsByActivity_ActivityIdAndUser_UserId(activityId, userId))throw new ApiNotFoundException("Not found any request " +
                        "of this user for this activity");
            }
        }
        Page<Request> listRequests = repository.findAllRequests(pageRequest, activityId, userId); //ได้เป็น Pageable ของ Request
        Page<RequestDto> pageRequestIncludeUsername = listRequests.map(request -> { //set username in each row
            RequestDto dto = modelMapper.map(request, RequestDto.class);
            dto.setUsername(request.getUser().getUsername());
            return dto;
        });
        PageRequestDto pageRequestDto = modelMapper.map(pageRequestIncludeUsername, PageRequestDto.class); //map ใส่ PageRequestDto
        return  pageRequestDto;
    }

    public ResponseEntity<?> createRequest(CreateRequestDto newRequest, BindingResult result)
            throws MethodArgumentNotValidException, ApiNotFoundException, ForbiddenException {
        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);
        boolean isThereActivity = activityRepository.existsById(newRequest.getActivityId());
        boolean isThereUser = userRepository.existsById(newRequest.getUserId());
        if(!isCurrentUserWillBeOwnRequest(newRequest))
            throw new ForbiddenException("You're not allowed to create other's request");
        if(!isThereActivity)
            throw new ApiNotFoundException("Activity not found!");
        if(!isThereUser)
            throw new ApiNotFoundException("User not found!");
        if(participantRepository.existsByActivity_ActivityIdAndUser_UserId(newRequest.getActivityId(), newRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You already participated in this Activity!");
        }
        if(repository.existsByActivity_ActivityIdAndUser_UserId(newRequest.getActivityId(), newRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You already requested to this Activity");
        }
        
        Request request = modelMapper.map(newRequest, Request.class);
        Request createdRequest = repository.saveAndFlush(request);
        return new ResponseEntity<RequestDto>(modelMapper.map(createdRequest, RequestDto.class), HttpStatus.CREATED);
    }

    public RequestDto update(UpdateRequestDto updateRequest, Integer activityId, Integer userId,
                             BindingResult result) throws ForbiddenException, MethodArgumentNotValidException {
        if(updateRequest.getMessage() != null) {
            if(updateRequest.getMessage().length() > 100)
                result.addError(messageSizeErrorObj);
        }
        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);
        Request existingRequest = repository.findByActivityActivityIdAndUser_UserId(activityId, userId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Request of Activity ID: " + activityId + " and User ID: " + userId + "does not exist !"));
        if(!isCurrentUserHost(existingRequest))
            throw new ForbiddenException("You're not allowed to edit this request");
        Request updatedRequest = mapRequest(existingRequest, updateRequest);
        return modelMapper.map(repository.saveAndFlush(updatedRequest), RequestDto.class);
    }

    private Request mapRequest(Request existingRequest, UpdateRequestDto updateRequestDto) {
        if(updateRequestDto.getMessage() != null) {
            existingRequest.setMessage(updateRequestDto.getMessage());
        }
        return existingRequest;
    }

    public void delete(Integer activityId, Integer userId) throws ForbiddenException {
        Request request = repository.findByActivityActivityIdAndUser_UserId(activityId, userId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Request of Activity ID: " + activityId + " and User ID: " + userId + "does not exist !"));
        if(!isCurrentUserHost(request))
            throw new ForbiddenException("You're not allowed to delete this request");
        repository.deleteByActivity_ActivityIdAndUser_UserId(activityId, userId);
    }

    private boolean isCurrentUserHost(Request request) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            boolean isCurrentAuthOwnRequest = request.getUser().getUserId() == currentAuthId;
            boolean isCurrentAuthOwnThisActivityOfParticipant = request.getActivity().getHostUser().getUserId() == currentAuthId;
            return isCurrentAuthOwnRequest || isCurrentAuthOwnThisActivityOfParticipant;
        }

        //System.out.println("Own of this activity: " + isCurrentAuthOwnThisActivityOfParticipant);
        //System.out.println(isCurrentAuthOwnRequest || isCurrentAuthOwnThisActivityOfParticipant);
        //System.out.println("Own request: " + isCurrentAuthOwnRequest);
    }

    private boolean isCurrentUserWillBeOwnRequest(CreateRequestDto createRequestDto) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return createRequestDto.getUserId() == currentAuthId;
        }

    }
}
