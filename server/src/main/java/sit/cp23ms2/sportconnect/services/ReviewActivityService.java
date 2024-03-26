package sit.cp23ms2.sportconnect.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.dtos.review_activity.CreateReviewActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_activity.PageReviewActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_activity.ReviewActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_activity.UpdateReviewActivityDto;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.entities.ReviewActivity;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.ReviewActivityRepository;
import sit.cp23ms2.sportconnect.repositories.ReviewUserRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;

@Service
public class ReviewActivityService {

    @Autowired
    ReviewActivityRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ActivityRepository activityRepository;

    public PageReviewActivityDto getReview(int pageNum, int pageSize, String sortBy, Integer activityId) {
        //Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<ReviewActivity> listReviews = repository.findAllReviewActivities(pageRequest, activityId);
        PageReviewActivityDto pageReviewDto = modelMapper.map(listReviews, PageReviewActivityDto.class);
        return pageReviewDto;
    }

    public ReviewActivity getById(Integer id) {
        ReviewActivity reviewActivity = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "ReviewActivity: " + id + " Not Found"));
        return reviewActivity;
    }

    public ReviewActivity Create(CreateReviewActivityDto newReview) {
        User user = userRepository.findById(newReview.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "ReviewActivity: " + newReview.getUserId() + " Not Found"));
        Activity activity = activityRepository.findById(newReview.getActivityId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "ReviewActivity: " + newReview.getActivityId() + " Not Found"));
        //ReviewActivity review = modelMapper.map(newReview, ReviewActivity.class);
        ReviewActivity review = new ReviewActivity();
        review.setActivity(activity);
        review.setUser(user);
        review.setComment(newReview.getComment());
        review.setRating(newReview.getRating());
        repository.saveAndFlush(review);
        return review;
    }

    public ReviewActivityDto update(UpdateReviewActivityDto updateReview, Integer id) {
        ReviewActivity review = getById(id);
        ReviewActivity updatedReview = mapReview(review, updateReview);
        return modelMapper.map(repository.saveAndFlush(updatedReview), ReviewActivityDto.class);
    }

    private ReviewActivity mapReview(ReviewActivity existReview, UpdateReviewActivityDto updateReview) {
        if(updateReview.getComment() != null)
            existReview.setComment(updateReview.getComment());
        if(updateReview.getRating() != null)
            existReview.setRating(updateReview.getRating());
        return  existReview;
    }

    public void delete(Integer id) {
        ReviewActivity review = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "ReviewActivity: " + id + " Not Found"));
        repository.deleteById(id);
    }
}
