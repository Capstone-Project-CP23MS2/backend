package sit.cp23ms2.sportconnect.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import sit.cp23ms2.sportconnect.dtos.notification.CreateNotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.NotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.PageNotificationDto;
import sit.cp23ms2.sportconnect.dtos.notification.UpdateNotificationDto;
import sit.cp23ms2.sportconnect.dtos.review_user.*;
import sit.cp23ms2.sportconnect.entities.ReviewUser;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.services.NotificationService;
import sit.cp23ms2.sportconnect.services.ReviewUserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reviews_users")
@Component
public class ReviewUserController {
    @Autowired
    ReviewUserService reviewUserService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    public PageReviewUserDto getReviewUser(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(defaultValue = "reviewId") String sortBy,
                                           @RequestParam(required = false) Integer userId) {

        return reviewUserService.getReviewUser(page, pageSize, sortBy, userId);

    }

    @GetMapping("/{id}")
    public ReviewUserDto getById(@PathVariable Integer id) {
        ReviewUser reviewUser = reviewUserService.getById(id);
        CustomReviewUserDto userReviewedObj = modelMapper.map(reviewUser.getUser(), CustomReviewUserDto.class);
        CustomReviewUserDto reviewerObj = modelMapper.map(reviewUser.getReviewer(), CustomReviewUserDto.class);

        ReviewUserDto reviewUserDto = modelMapper.map(reviewUser, ReviewUserDto.class);
        reviewUserDto.setUserReviewed(userReviewedObj);
        reviewUserDto.setReviewer(reviewerObj);
        return reviewUserDto;
    }

    @PostMapping
    public ReviewUserDto createReviewUser(@Valid @ModelAttribute CreateReviewUserDto createReviewUserDto, BindingResult result)
            throws ForbiddenException, MethodArgumentNotValidException {
        //return modelMapper.map(reviewUserService.Create(createReviewUserDto), ReviewUserDto.class);
        ReviewUser reviewUser = reviewUserService.Create(createReviewUserDto, result);
        CustomReviewUserDto userReviewedObj = modelMapper.map(reviewUser.getUser(), CustomReviewUserDto.class);
        CustomReviewUserDto reviewerObj = modelMapper.map(reviewUser.getReviewer(), CustomReviewUserDto.class);

        ReviewUserDto reviewUserDto = modelMapper.map(reviewUser, ReviewUserDto.class);
        reviewUserDto.setUserReviewed(userReviewedObj);
        reviewUserDto.setReviewer(reviewerObj);
        return reviewUserDto;
    }

    @PatchMapping("/{id}")
    public ReviewUserDto update(@Valid @ModelAttribute UpdateReviewUserDto updateReviewUserDto,
                                @PathVariable Integer id, BindingResult result) throws ForbiddenException, MethodArgumentNotValidException {
        return reviewUserService.update(updateReviewUserDto, id, result);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) throws ForbiddenException {
        reviewUserService.delete(id);
    }
}
