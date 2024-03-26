package sit.cp23ms2.sportconnect.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import sit.cp23ms2.sportconnect.dtos.review_activity.CreateReviewActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_activity.PageReviewActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_activity.ReviewActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_activity.UpdateReviewActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_user.CreateReviewUserDto;
import sit.cp23ms2.sportconnect.dtos.review_user.PageReviewUserDto;
import sit.cp23ms2.sportconnect.dtos.review_user.ReviewUserDto;
import sit.cp23ms2.sportconnect.dtos.review_user.UpdateReviewUserDto;
import sit.cp23ms2.sportconnect.entities.ReviewActivity;
import sit.cp23ms2.sportconnect.services.ReviewActivityService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reviews_activities")
@Component
public class ReviewActivityController {
    @Autowired
    ReviewActivityService reviewActivityService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    public PageReviewActivityDto getReviewUser(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               @RequestParam(defaultValue = "reviewId") String sortBy,
                                               @RequestParam(required = false) Integer activityId) {

        return reviewActivityService.getReview(page, pageSize, sortBy, activityId);

    }

    @GetMapping("/{id}")
    public ReviewActivityDto getById(@PathVariable Integer id) {
        return modelMapper.map(reviewActivityService.getById(id), ReviewActivityDto.class) ;
    }

    @PostMapping
    public ReviewActivityDto createReviewUser(@Valid @ModelAttribute CreateReviewActivityDto createReviewActivityDto) {
        return modelMapper.map(reviewActivityService.Create(createReviewActivityDto), ReviewActivityDto.class);
    }

    @PatchMapping("/{id}")
    public ReviewActivityDto update(@Valid @ModelAttribute UpdateReviewActivityDto updateReviewActivityDto,
                                @PathVariable Integer id) {
        return reviewActivityService.update(updateReviewActivityDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        reviewActivityService.delete(id);
    }
}
