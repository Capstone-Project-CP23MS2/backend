package sit.cp23ms2.sportconnect.services;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.ResponseEntity;

import sit.cp23ms2.sportconnect.controllers.SearchTest;
import sit.cp23ms2.sportconnect.dtos.activity.*;
import sit.cp23ms2.sportconnect.entities.*;
import sit.cp23ms2.sportconnect.enums.NotificationType;
import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
import sit.cp23ms2.sportconnect.exceptions.type.BadRequestException;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.*;
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
import sit.cp23ms2.sportconnect.utils.AuthenticationUtil;


import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
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
    @Autowired NotificationRepository notificationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private AuthenticationUtil authenticationUtil;
    @Autowired
    LocationService locationService;
    @Autowired
    FileRepository fileRepository;

    //nameError
    final private FieldError titleErrorObj = new FieldError("createActivityDto",
            "title", "Title already used in other Activity now! Please use other title");
    final private FieldError titleErrorObj2 = new FieldError("updateActivityDto",
            "title", "Title already used in other Activity now! Please use other title");
    final private FieldError titleSizeErrorObj = new FieldError("updateActivityDto",
            "title", "Title size must not over 100");

    public PageActivityDto getActivity(int pageNum, int pageSize, String sortBy, Set<Integer> categoryIds, String title,
                                       Integer activityId, Integer hostUserId, Integer userId, String dateStatus, String date, String orderBy) throws BadRequestException {
        //sort and order
        Sort sort = null;
        if(!orderBy.equals("ASC") && !orderBy.equals("DESC") && !orderBy.equals("asc") && !orderBy.equals("desc")) {
                throw new BadRequestException("parameter orderBy is not these values: ASC, DESC, asc, desc");
            }
            if(orderBy.equals("ASC") || orderBy.equals("asc"))
                sort = JpaSort.unsafe(Sort.Direction.ASC, "\"" + sortBy + "\"");
            if(orderBy.equals("DESC") || orderBy.equals("desc"))
                sort = JpaSort.unsafe(Sort.Direction.DESC, "\"" + sortBy + "\"");
        //query
        Pageable pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page<Activity> listActivities;
        if(categoryIds != null) { //check if category filter
            listActivities = repository.findAllActivities(pageRequest, categoryIds, title, activityId, hostUserId, userId, dateStatus, date);
        } else {
            listActivities = repository.findAllActivitiesNoCategoryFilter(pageRequest, title, activityId, hostUserId, userId, dateStatus, date);
            System.out.println(title);
        }
        Page<ActivityDto> listActivitiesCustomDto = listActivities.map(activity -> { //custom ค่าอื่นๆมาใส่ใน dto
            ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);
            //Set<File> filesInThisActivity = this.getActivityFiles(activity.getActivityId());
            //set category name in dto
            activityDto.setCategoryName(activity.getCategoryId().getName());
            //set users_activities in dto with custom field
            Set<CustomUserActivityDto> userSets = activity.getUsers().stream().map(user -> {
                CustomUserActivityDto userSet = modelMapper.map(user, CustomUserActivityDto.class); // เอา user ที่มีทุก field มา map เข้าไปแค่ 3 fields ทีละ user
                return userSet;
            }).collect(Collectors.toSet());
            Set<CustomFileActivityDto> fileSets = this.getActivityFiles(activity.getActivityId()).stream().map(file -> {
                CustomFileActivityDto fileSet = modelMapper.map(file, CustomFileActivityDto.class); // เอา File ที่มีทุก field มา map เข้าไปแค่ 2 fields ทีละ file
                return fileSet;
            }).collect(Collectors.toSet());
            activityDto.setUsers(userSets);
            activityDto.setFiles(fileSets);
            return activityDto;
        });

        PageActivityDto pageActivityDto = modelMapper.map(listActivitiesCustomDto, PageActivityDto.class); //map ใส่ PageUserDto
        return  pageActivityDto;
    }

    //No Pagination
    public List<ActivityDto> getActivityNoPaging(Set<Integer> categoryIds, String title, Integer activityId,
                                                 Integer hostUserId, Integer userId, String dateStatus, String date,
                                                 Double lat, Double lng, Integer radius, String orderBy) throws BadRequestException {
        List<Activity> activities;
        String pointText = new String();
        if(lat != null && lng != null && radius != null) {
            pointText = "'POINT(" + lat + " " + lng + ")'";
            System.out.println(pointText);
        }
        if (categoryIds != null) {
            activities = repository.findAllListActivities(categoryIds, title, activityId, hostUserId, userId, dateStatus, date, lat, lng, radius);
        } else {
            activities = repository.findAllListActivitiesNoCategoryFilter(title, activityId, hostUserId, userId, dateStatus, date, lat, lng, radius);
        }

        List<ActivityDto> activityDtos = activities.stream().map(activity -> {
            ActivityDto activityDto = modelMapper.map(activity, ActivityDto.class);

            // Set category name in DTO
            activityDto.setCategoryName(activity.getCategoryId().getName());

            // Set users in DTO with custom field
            Set<CustomUserActivityDto> userSets = activity.getUsers().stream().map(user -> {
                CustomUserActivityDto userSet = modelMapper.map(user, CustomUserActivityDto.class);
                return userSet;
            }).collect(Collectors.toSet());
            activityDto.setUsers(userSets);

            // Set files in DTO with custom field
            Set<CustomFileActivityDto> fileSets = this.getActivityFiles(activity.getActivityId()).stream().map(file -> {
                CustomFileActivityDto fileSet = modelMapper.map(file, CustomFileActivityDto.class);
                return fileSet;
            }).collect(Collectors.toSet());
            activityDto.setFiles(fileSets);

            return activityDto;
        }).collect(Collectors.toList());
        if(!orderBy.equals("ASC") && !orderBy.equals("DESC") && !orderBy.equals("asc") && !orderBy.equals("desc")) {
            throw new BadRequestException("parameter orderBy is not these values: ASC, DESC, asc, desc");
        }
        if(orderBy.equals("ASC") || orderBy.equals("asc"))
            activityDtos.sort(Comparator.comparing(ActivityDto::getActivityId));
        if(orderBy.equals("DESC") || orderBy.equals("desc"))
            activityDtos.sort(Comparator.comparing(ActivityDto::getActivityId).reversed());


        return activityDtos;
    }

    public List<Activity> getTest(SearchTest searchTest) {
        return repository.findAllActivitiesNoCategoryFilterNoPage(searchTest.getTitle());
//        return repository.findAllActivitiesNoCategoryFilterNoPage(searchTest.getTitle(), searchTest.getPlace());
    }

    public Activity getById(Integer id) {
        return //repository.findById(id).orElseThrow(() -> new ApiNotFoundException("Activity not found!"));
                repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Activity create(CreateActivityDto newActivity, BindingResult result) throws MethodArgumentNotValidException, ForbiddenException {
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
        //send batch of notifications to users who interest this category
        Location location = locationRepository.findById(newActivity.getLocationId()).orElseThrow(() -> new ApiNotFoundException("Location not found!"));
        List<User> usersInterestThisActivityCategory = userRepository.findUsersCategoriesInterests(newActivity.getCategoryId(), newActivity.getHostUserId(), location.getLongitude(), location.getLatitude()).orElseThrow(() -> new ApiNotFoundException("User not found!"));
        List<Notification> notifications = new ArrayList<>();
        for(User user : usersInterestThisActivityCategory) {
            Notification notification = new Notification();
            notification.setTargetId(user);
            notification.setActivity(createdActivity);
            notification.setUnRead(true);
            notification.setType(NotificationType.recommend);
            notification.setMessage("We found new Activity near you that you may interested! - " + newActivity.getTitle());
            notification.setCreatedAt(createdActivity.getCreatedAt());
            notifications.add(notification);
        }
        notificationRepository.saveAllAndFlush(notifications);

        activityParticipantRepository.insertWithEnum(createdActivity.getHostUser().getUserId()
                , createdActivity.getActivityId(), "waiting", "going", createdActivity.getCreatedAt());

        return createdActivity;
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
        String notificationMessage = "";
        ActivityDto updatedActivityDto = mapActivity(activity, updateActivity, notificationMessage, id, activity.getHostUser().getUserId());





        return updatedActivityDto;
    }

    public ActivityDto mapActivity(Activity existingActivity, UpdateActivityDto updateActivity, String notificationMessage, Integer id, Integer hostId) {
        ActivityDto activityDto = new ActivityDto();
        if((updateActivity.getTitle() != null && !updateActivity.getTitle().trim().equals("")) || (updateActivity.getCategoryId() != null) || (updateActivity.getDescription() != null && !updateActivity.getDescription().trim().equals("")) || (updateActivity.getLocationId() != null) || (updateActivity.getDateTime() != null && !updateActivity.getDateTime().toString().trim().equals("")) || (updateActivity.getDuration() != null) || (updateActivity.getNoOfMembers() != null)) {
            notificationMessage = "Activity that you're participating are changed: " + " | ";
            if(updateActivity.getTitle() != null && !updateActivity.getTitle().trim().equals("")) { //Set Title
                notificationMessage = notificationMessage + "Title was changed from `" + existingActivity.getTitle() + "` to `" + updateActivity.getTitle() + "`" + "| ".replace('`', '"');
                existingActivity.setTitle(updateActivity.getTitle());
            }
            if(updateActivity.getCategoryId() != null) { //Set Category
                Category newCategory = categoryRepository.findById(updateActivity.getCategoryId()).orElseThrow(() -> new ApiNotFoundException("Category not found!"));
                notificationMessage = notificationMessage + "Category: `" + existingActivity.getCategoryId().getName() + "` -> `" + newCategory.getName() + "`" + "| ".replace('`', '"');
                existingActivity.setCategoryId(newCategory);
            }
            if(updateActivity.getDescription() != null && !updateActivity.getDescription().trim().equals("")) { //SET Description
                notificationMessage = notificationMessage + "Desc: `" + existingActivity.getDescription() + "` -> `" + updateActivity.getDescription() + "`" + "| ".replace('`', '"');
                existingActivity.setDescription(updateActivity.getDescription());
            }

            if(updateActivity.getLocationId() != null){ //Set Location ID
                Location newLocation = locationRepository.findById(updateActivity.getLocationId()).orElseThrow(() -> new ApiNotFoundException("Location not found!"));
                notificationMessage = notificationMessage + "Location: `" + existingActivity.getLocation().getName() + "` -> `" + newLocation.getName() + "`" + "| ".replace('`', '"');
                existingActivity.setLocation(newLocation);
            }
            if(updateActivity.getDateTime() != null && !updateActivity.getDateTime().toString().trim().equals("")) {//Set Date & Time
                notificationMessage = notificationMessage + "Date: `" + existingActivity.getDateTime() + "` -> `" + updateActivity.getDateTime() + "`" + "| ".replace('`', '"');
                existingActivity.setDateTime(updateActivity.getDateTime());
            }
            if(updateActivity.getDuration() != null) { //Set Duration
                notificationMessage = notificationMessage + "Duration: `" + existingActivity.getDuration() + "` -> `" + updateActivity.getDuration() + "`" + "| ".replace('`', '"');
                existingActivity.setDuration(updateActivity.getDuration());
            }
            if(updateActivity.getNoOfMembers() != null) { //Set NoOfMembers
                notificationMessage = notificationMessage + "Members: `" + existingActivity.getNoOfMembers() + "` -> `" + updateActivity.getNoOfMembers() + "`" + "| ".replace('`', '"');
                existingActivity.setNoOfMembers(updateActivity.getNoOfMembers());
            }
            if(updateActivity.getLineGroupUrl() != null) { //Set Line Group Url
                notificationMessage = notificationMessage + "Line: `" + existingActivity.getLineGroupUrl() + "` -> `" + updateActivity.getLineGroupUrl() + "`" + "| ".replace('`', '"');
                existingActivity.setLineGroupUrl(updateActivity.getLineGroupUrl());
            }
            activityDto = modelMapper.map(repository.saveAndFlush(existingActivity), ActivityDto.class);
            List<Notification> newNotifications = new ArrayList<>();
            List<User> participants = userRepository.findUsersParticipantsInActivity(id,hostId).orElseThrow(() -> new ApiNotFoundException("User not found!"));
            for(User user : participants) {
                Notification notification = new Notification();
                notification.setTargetId(user);
                notification.setActivity(existingActivity);
                notification.setUnRead(true);
                notification.setType(NotificationType.update_activity);
                notification.setMessage(notificationMessage);
                notification.setCreatedAt(Instant.now());
                newNotifications.add(notification);
            }
            notificationRepository.saveAllAndFlush(newNotifications);
        }

        return activityDto;
    }

    public void delete(Integer id) throws ForbiddenException {
        Activity activity = repository.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + "does not exist !"));
        if(!isCurrentUserHost(activity))
            throw new ForbiddenException("You're not allowed to delete this activity");
        //UPDATE OLD NOTI + ADD NEW NOTI BEFORE ACTIVITY IS DELETED
        List<Notification> oldNotifications = notificationRepository.findAllByActivity_ActivityId(id);
        List<Notification> updatedNotificationsBeforeDelete = new ArrayList<>();
        List<Notification> newNotifications = new ArrayList<>();
        List<User> participants = userRepository.findUsersParticipantsInActivity(id, activity.getHostUser().getUserId()).orElseThrow(() -> new ApiNotFoundException("User not found!"));
        for(Notification notification : oldNotifications) {
            notification.setActivity(null);
            updatedNotificationsBeforeDelete.add(notification);
        }
//        System.out.println(participants);
        notificationRepository.saveAllAndFlush(updatedNotificationsBeforeDelete);

        for(User user : participants) {
//            System.out.println(user.getUserId());
            Notification notification = new Notification();
            notification.setTargetId(user);
            notification.setActivity(null);
            notification.setUnRead(true);
            notification.setType(NotificationType.delete_activity);
            notification.setMessage("Your participant's Activity is deleted by Host - " + activity.getTitle());
            notification.setCreatedAt(Instant.now());
            newNotifications.add(notification);
        }
        System.out.println(newNotifications);
        notificationRepository.saveAllAndFlush(newNotifications);
        repository.deleteById(id);
    }

    public Set<File> getActivityFiles(Integer activityId) {
        return fileRepository.findAllByActivityFile_ActivityId(activityId);
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
