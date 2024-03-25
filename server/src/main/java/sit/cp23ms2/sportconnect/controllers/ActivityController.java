package sit.cp23ms2.sportconnect.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import sit.cp23ms2.sportconnect.dtos.activity.ActivityDto;
import sit.cp23ms2.sportconnect.dtos.activity.CreateActivityDto;
import sit.cp23ms2.sportconnect.dtos.activity.PageActivityDto;
import sit.cp23ms2.sportconnect.dtos.activity.UpdateActivityDto;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
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

@RestController
@RequestMapping("/api/activities")
@Component
public class ActivityController {
    @Autowired
    public ActivityService activityService;
    @Autowired
    public ModelMapper modelMapper;

    @GetMapping
    public PageActivityDto getActivity(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(defaultValue = "\"activityId\"") String sortBy,
                                       @RequestParam(required = false)Set<Integer> categoryIds,
                                       @RequestParam(required = false)String title
                                       //@RequestParam(required = false)String place
            , HttpServletResponse response) throws IOException {
        //response.sendRedirect("https://google.com");

        return activityService.getActivity(page, pageSize, sortBy, categoryIds, title);
    }

    @GetMapping("/{id}")
    public ActivityDto getActivityById(@PathVariable Integer id) {
        return modelMapper.map(activityService.getById(id), ActivityDto.class);
    }

    @PostMapping
    public ActivityDto createActivity(@Valid @ModelAttribute CreateActivityDto newActivity, BindingResult result) throws MethodArgumentNotValidException,
            ForbiddenException{
        return activityService.create(newActivity, result);
    }

    @PostMapping("/search")
    public PageActivityDto searchTest(@RequestBody SearchTest searchTest,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int pageSize,
                                  @RequestParam(defaultValue = "title") String sortBy) throws MethodArgumentNotValidException {
        return activityService.getActivity(page, pageSize, sortBy, null, searchTest.getTitle());
    }

    @PostMapping("/search2")
    public ResponseEntity<List<Activity>> searchTest2(@RequestBody SearchTest searchTest
                                      ) throws MethodArgumentNotValidException {
        return ResponseEntity.ok(activityService.getTest(searchTest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateActivity(@Valid @ModelAttribute UpdateActivityDto updateActivityDto,
                                      @PathVariable Integer id, BindingResult result) throws MethodArgumentNotValidException, ForbiddenException {

        try {
            return ResponseEntity.ok(activityService.update(updateActivityDto, id, result));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (HttpClientErrorException.UnsupportedMediaType e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage() + " Invalid Data Format");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage() + " Internal Server Error");
        }

    }

    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Integer id) throws ForbiddenException {
        activityService.delete(id);
    }
}
