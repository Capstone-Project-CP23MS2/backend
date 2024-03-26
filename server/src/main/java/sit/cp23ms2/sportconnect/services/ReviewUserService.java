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

import sit.cp23ms2.sportconnect.dtos.review_user.CreateReviewUserDto;
import sit.cp23ms2.sportconnect.dtos.review_user.PageReviewUserDto;
import sit.cp23ms2.sportconnect.dtos.review_user.ReviewUserDto;
import sit.cp23ms2.sportconnect.dtos.review_user.UpdateReviewUserDto;

import sit.cp23ms2.sportconnect.entities.ReviewUser;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.ReviewUserRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;

@Service
public class ReviewUserService {
    @Autowired
    ReviewUserRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ActivityRepository activityRepository;

    public PageReviewUserDto getReviewUser(int pageNum, int pageSize, String sortBy, Integer userId) {
        //Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<ReviewUser> listReviews = repository.findAllReviewUsers(pageRequest, userId);
        PageReviewUserDto pageReviewDto = modelMapper.map(listReviews, PageReviewUserDto.class);
        return pageReviewDto;
    }

    public ReviewUser getById(Integer id) {
        ReviewUser review = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Review User: " + id + " Not Found"));
        return review;
    }

    public ReviewUser Create(CreateReviewUserDto newReview) {
        User user = userRepository.findById(newReview.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User Id: " + newReview.getUserId() + " Not Found"));
        User reviewerId = userRepository.findById(newReview.getReviewerId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Reviewer Id: " + newReview.getReviewerId() + " Not Found"));
//        ReviewUser review = modelMapper.map(newReview, ReviewUser.class);
        ReviewUser review = new ReviewUser();
        review.setUser(user);
        review.setReviewer(reviewerId);
        review.setComment(newReview.getComment());
        repository.saveAndFlush(review);
        return review;
    }

    public ReviewUserDto update(UpdateReviewUserDto updateReview, Integer id) {
        ReviewUser review = getById(id);
        ReviewUser updatedReview = mapReview(review, updateReview);
        return modelMapper.map(repository.saveAndFlush(updatedReview), ReviewUserDto.class);
    }

    private ReviewUser mapReview(ReviewUser existReview, UpdateReviewUserDto updateReview) {
        if(updateReview.getComment() != null && !updateReview.getComment().trim().equals(""))
            existReview.setComment(updateReview.getComment());
        return  existReview;
    }

    public void delete(Integer id) {
        ReviewUser review = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Review User: " + id + " Not Found"));
        repository.deleteById(id);
    }
}
