package sit.cp23ms2.sportconnect.services;

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

import sit.cp23ms2.sportconnect.dtos.activity.ActivityDto;
import sit.cp23ms2.sportconnect.dtos.review_user.*;

import sit.cp23ms2.sportconnect.entities.Notification;
import sit.cp23ms2.sportconnect.entities.ReviewUser;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.ActivityRepository;
import sit.cp23ms2.sportconnect.repositories.ReviewUserRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;
import sit.cp23ms2.sportconnect.utils.AuthenticationUtil;

import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    private AuthenticationUtil authenticationUtil;

    final private FieldError messageSizeErrorObj = new FieldError("updateReviewUserDto",
            "message", "Message size must not over 255");

    public PageReviewUserDto getReviewUser(int pageNum, int pageSize, String sortBy, Integer userId) {
        //Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<ReviewUser> listReviews = repository.findAllReviewUsers(pageRequest, userId);
        Page<ReviewUserDto> listReviewsCustomDto = listReviews.map(review -> { //Custom ลงไปบน Dto
            ReviewUserDto reviewUserDto = modelMapper.map(review, ReviewUserDto.class);
            CustomReviewUserDto userReviewedObj = modelMapper.map(review.getUser(), CustomReviewUserDto.class);
            CustomReviewUserDto reviewerObj = modelMapper.map(review.getReviewer(), CustomReviewUserDto.class);
            reviewUserDto.setUserReviewed(userReviewedObj);
            reviewUserDto.setReviewer(reviewerObj);
            return reviewUserDto;
        });
        PageReviewUserDto pageReviewDto = modelMapper.map(listReviewsCustomDto, PageReviewUserDto.class);
        return pageReviewDto;
    }

    public ReviewUser getById(Integer id) {
        ReviewUser review = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Review User: " + id + " Not Found"));
        return review;
    }

    public ReviewUser Create(CreateReviewUserDto newReview, BindingResult result) throws ForbiddenException, MethodArgumentNotValidException {

        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);
        if(!isCurrentUserGoingToBeReviewer(newReview))
            throw new ForbiddenException("You're not allowed to create Review since you're not the reviewer of this review !");
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

    public ReviewUserDto update(UpdateReviewUserDto updateReview, Integer id, BindingResult result) throws
            ForbiddenException, MethodArgumentNotValidException {
        if(updateReview.getComment() != null) {
            if(updateReview.getComment().length() > 255)
                result.addError(messageSizeErrorObj);
        }
        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);
        ReviewUser review = getById(id);
        if(!isCurrentUserHostReview(review))
            throw new ForbiddenException("You're not allowed to edit this Review since you're not the reviewer of this review !");
        ReviewUser updatedReview = mapReview(review, updateReview);
        return modelMapper.map(repository.saveAndFlush(updatedReview), ReviewUserDto.class);
    }

    private ReviewUser mapReview(ReviewUser existReview, UpdateReviewUserDto updateReview) {
        if(updateReview.getComment() != null && !updateReview.getComment().trim().equals(""))
            existReview.setComment(updateReview.getComment());
        return  existReview;
    }

    public void delete(Integer id) throws ForbiddenException {
        ReviewUser review = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Review User: " + id + " Not Found"));
        if(!isCurrentUserHostReview(review))
            throw new ForbiddenException("You're not allowed to delete this Review since you're not the reviewer of this review !");
        repository.deleteById(id);
    }

    private boolean isCurrentUserHostReview(ReviewUser reviewUser) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return reviewUser.getReviewer().getUserId() == currentAuthId;
        }
    }
    private boolean isCurrentUserGoingToBeReviewer(CreateReviewUserDto newReview) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return newReview.getReviewerId() == currentAuthId;
        }
    }
}
