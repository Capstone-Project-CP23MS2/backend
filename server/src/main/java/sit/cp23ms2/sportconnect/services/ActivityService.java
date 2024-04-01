package sit.cp23ms2.sportconnect.services;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;

import sit.cp23ms2.sportconnect.controllers.SearchTest;
import sit.cp23ms2.sportconnect.dtos.activity.*;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.entities.Category;
import sit.cp23ms2.sportconnect.entities.Location;
import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.ActivityParticipantRepository;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.repositories.LocationRepository;
import sit.cp23ms2.sportconnect.utils.AuthenticationUtil;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class ActivityService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ActivityRepository repository;
    @Autowired
    private ActivityParticipantRepository activityParticipantRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    private AuthenticationUtil authenticationUtil;
    @Autowired
    LocationService locationService;

    //nameError
    final private FieldError titleErrorObj = new FieldError("createActivityDto",
            "title", "Title already used in other Activity now! Please use other title");
    final private FieldError titleErrorObj2 = new FieldError("updateActivityDto",
            "title", "Title already used in other Activity now! Please use other title");
    final private FieldError titleSizeErrorObj = new FieldError("updateActivityDto",
            "title", "Title size must not over 100");

    public PageActivityDto getActivity(int pageNum, int pageSize, String sortBy, Set<Integer> categoryIds, String title) {
        //Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<Activity> listActivities;
        if(categoryIds != null) {
            listActivities = repository.findAllActivities(pageRequest, categoryIds, title); //ได้เป็น Pageable ของ User\
        } else {
            listActivities = repository.findAllActivitiesNoCategoryFilter(pageRequest, title);
            System.out.println(title);
        }
        Page<ActivityDto> listActivitiesCustomDto = listActivities.map(activity -> { //custom ค่าอื่นๆมาใส่ใน dto
            ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
            //set category name in dto
            activityDto.setCategoryName(activity.getCategoryId().getName());
            //set users_activities in dto with custom field
            Set<CustomUserActivityDto> userSets = activity.getUsers().stream().map(user -> {
                CustomUserActivityDto userSet = modelMapper.map(user, CustomUserActivityDto.class); // เอา user ที่มีทุก field มา map เข้าไปแค่ 3 fields ทีละ user
                return userSet;
            }).collect(Collectors.toSet());
            activityDto.setUsers(userSets);
            return activityDto;
        });

        PageActivityDto pageActivityDto = modelMapper.map(listActivitiesCustomDto, PageActivityDto.class); //map ใส่ PageUserDto
        return  pageActivityDto;
    }

    public List<Activity> getTest(SearchTest searchTest) {
        return repository.findAllActivitiesNoCategoryFilterNoPage(searchTest.getTitle());
//        return repository.findAllActivitiesNoCategoryFilterNoPage(searchTest.getTitle(), searchTest.getPlace());
    }

    public Activity getById(Integer id) {
        return //repository.findById(id).orElseThrow(() -> new ApiNotFoundException("Activity not found!"));
                repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public ActivityDto create(CreateActivityDto newActivity, BindingResult result) throws MethodArgumentNotValidException, ForbiddenException {
        //Error validation
        if(!isCurrentUserWillBeHostActivity(newActivity))
            throw new ForbiddenException("You're not allowed to create other's activity");
        if(repository.existsByTitle(newActivity.getTitle())) {
            result.addError(titleErrorObj);
        }
        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);

        Activity activity = modelMapper.map(newActivity, Activity.class);
        activity.setLocation(locationService.getById(newActivity.getLocationId()));
        //save Activity to database
        Activity createdActivity = repository.saveAndFlush(activity);
        //create participant (because hostUser is also participant)
        ActivityParticipant activityParticipant = new ActivityParticipant();
        activityParticipantRepository.insertWithEnum(createdActivity.getHostUser().getUserId()
                , createdActivity.getActivityId(), "ready", createdActivity.getCreatedAt());

        return modelMapper.map(createdActivity, ActivityDto.class);
    }

    public ActivityDto update(UpdateActivityDto updateActivity, Integer id, BindingResult result) throws MethodArgumentNotValidException,
            ForbiddenException {
        Activity activity = getById(id);
        System.out.println(activity.getTitle());
        if(!isCurrentUserHost(activity))
            throw new ForbiddenException("You're not allowed to edit this activity");
        if(updateActivity.getTitle() != null && !updateActivity.getTitle().trim().equals("")) {
            if(repository.existsByTitleAndActivityIdNot(updateActivity.getTitle(), id)) { //Check duplicate title
                result.addError(titleErrorObj2);
            }
            if(updateActivity.getTitle().length() > 100)
                result.addError(titleSizeErrorObj);
        }

        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);

        //System.out.println("โย่: " + updateActivity.getTitle());

        Activity updatedActivity = mapActivity(activity, updateActivity);

        return modelMapper.map(repository.saveAndFlush(updatedActivity), ActivityDto.class);
    }

    public Activity mapActivity(Activity existingActivity, UpdateActivityDto updateActivity) {
        if(updateActivity.getTitle() != null && !updateActivity.getTitle().trim().equals("")) { //Set Title
            System.out.println("สวัสดี: "+ updateActivity.getTitle());
            existingActivity.setTitle(updateActivity.getTitle());
        }
        if(updateActivity.getCategoryId() != null) { //Set Category
            Category newCategory = categoryRepository.findById(updateActivity.getCategoryId()).orElseThrow(() -> new ApiNotFoundException("Category not found!"));
            existingActivity.setCategoryId(newCategory);
        }
        if(updateActivity.getDescription() != null && !updateActivity.getDescription().trim().equals("")) //Set Description
            existingActivity.setDescription(updateActivity.getDescription());
        if(updateActivity.getLocationId() != null){ //Set Location ID
            Location newLocation = locationRepository.findById(updateActivity.getLocationId()).orElseThrow(() -> new ApiNotFoundException("Location not found!"));
            existingActivity.setLocation(newLocation);
        }
        if(updateActivity.getDateTime() != null && !updateActivity.getDateTime().toString().trim().equals("")) //Set Date & Time
            existingActivity.setDateTime(updateActivity.getDateTime());
        if(updateActivity.getDuration() != null)
            existingActivity.setDuration(updateActivity.getDuration());
        if(updateActivity.getNoOfMembers() != null) {
            existingActivity.setNoOfMembers(updateActivity.getNoOfMembers());
        }
        return existingActivity;
    }

    public void delete(Integer id) throws ForbiddenException {
        Activity activity = repository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + "does not exist !"));
        if(!isCurrentUserHost(activity))
            throw new ForbiddenException("You're not allowed to delete this activity");
        repository.deleteById(id);
    }

    private boolean isCurrentUserHost(Activity activity) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return activity.getHostUser().getUserId() == currentAuthId;
        }
    }

    private boolean isCurrentUserWillBeHostActivity(CreateActivityDto createActivityDto) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return createActivityDto.getHostUserId() == currentAuthId;
        }
    }
}
