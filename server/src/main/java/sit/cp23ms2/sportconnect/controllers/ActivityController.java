package sit.cp23ms2.sportconnect.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import sit.cp23ms2.sportconnect.dtos.activity.*;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.FileRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;
import sit.cp23ms2.sportconnect.services.ActivityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
@Component
public class ActivityController {
    @Autowired
    public ActivityService activityService;
    @Autowired
    public ModelMapper modelMapper;
    @Autowired
    ActivityRepository repository;

    @GetMapping
    public PageActivityDto getActivity(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(defaultValue = "\"activityId\"") String sortBy,
                                       @RequestParam(required = false)Set<Integer> categoryIds,
                                       @RequestParam(required = false)String title,
                                       @RequestParam(required = false)Integer activityId,
                                       @RequestParam(required = false)Integer hostId,
                                       @RequestParam(required = false)Integer userId,
                                       @RequestParam(required = false)String dateStatus,
                                       @RequestParam(required = false)String date
                                       //@RequestParam(required = false)String place
            , HttpServletResponse response) throws IOException {
        //response.sendRedirect("https://google.com");

        return activityService.getActivity(page, pageSize, sortBy, categoryIds, title, activityId, hostId, userId, dateStatus, date);
    }

    @GetMapping("/getList")
    public List<ActivityDto> getActivity(
                                       @RequestParam(required = false)Set<Integer> categoryIds,
                                       @RequestParam(required = false)String title,
                                       @RequestParam(required = false)Integer activityId,
                                       @RequestParam(required = false)Integer hostId,
                                       @RequestParam(required = false)Integer userId,
                                       @RequestParam(required = false)String dateStatus,
                                       @RequestParam(required = false)String date,
                                       @RequestParam(required = false)Double lat,
                                       @RequestParam(required = false)Double lng,
                                       @RequestParam(required = false)Integer radius
                                       //@RequestParam(required = false)String place
            , HttpServletResponse response) throws IOException {
        //response.sendRedirect("https://google.com");

        return activityService.getActivityNoPaging(categoryIds, title, activityId, hostId, userId, dateStatus, date, lat, lng, radius);
    }

    @GetMapping("/{id}")
    public ActivityDto getActivityById(@PathVariable Integer id) {
        Activity activity = activityService.getById(id);
        Set<CustomFileActivityDto> fileSets = activityService.getActivityFiles(activity.getActivityId()).stream().map(file -> {
            CustomFileActivityDto fileSet = modelMapper.map(file, CustomFileActivityDto.class); // เอา File ที่มีทุก field มา map เข้าไปแค่ 2 fields ทีละ file
            return fileSet;
        }).collect(Collectors.toSet());
        ActivityDto activityDto = modelMapper.map(activityService.getById(id), ActivityDto.class);
        activityDto.setFiles(fileSets);
        return activityDto;
    }

    @PostMapping
    public ActivityDto createActivity(@Valid @ModelAttribute CreateActivityDto newActivity, BindingResult result) throws MethodArgumentNotValidException,
            ForbiddenException{
        Activity createdActivity = activityService.create(newActivity, result);
        ActivityDto dto = modelMapper.map(createdActivity, ActivityDto.class);
        dto.setMemberCounts(1);

        return dto;
    }

    @PostMapping("/search")
    public PageActivityDto searchTest(@RequestBody SearchTest searchTest,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestParam(defaultValue = "title") String sortBy,
                                      @RequestParam(required = false) Integer activityId,
                                      @RequestParam(required = false)Integer hostId,
                                      @RequestParam(required = false) Integer userId,
                                      @RequestParam(required = false)String dateStatus,
                                      @RequestParam(required = false)String date) throws MethodArgumentNotValidException {
        return activityService.getActivity(page, pageSize, sortBy, null, searchTest.getTitle(), activityId, hostId, userId, dateStatus, date);
    }

    @PostMapping("/search2")
    public ResponseEntity<List<Activity>> searchTest2(@RequestBody SearchTest searchTest
                                      ) throws MethodArgumentNotValidException {
        return ResponseEntity.ok(activityService.getTest(searchTest));
    }

    @PatchMapping("/{id}")
    public ActivityDto updateActivity(@Valid @ModelAttribute UpdateActivityDto updateActivityDto,
                                      @PathVariable Integer id, BindingResult result) throws MethodArgumentNotValidException, ForbiddenException {

//        try {
//            return ResponseEntity.ok(activityService.update(updateActivityDto, id, result));
//        } catch (DataIntegrityViolationException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        } catch (HttpClientErrorException.UnsupportedMediaType e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage() + " Invalid Data Format");
//        }
        return activityService.update(updateActivityDto, id, result);
    }

    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Integer id) throws ForbiddenException {
        activityService.delete(id);
    }
}
