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
import sit.cp23ms2.sportconnect.dtos.user_interests.CreateInterestBatchDto;
import sit.cp23ms2.sportconnect.dtos.user_interests.CreateInterestDto;
import sit.cp23ms2.sportconnect.dtos.user_interests.InterestDto;
import sit.cp23ms2.sportconnect.dtos.user_interests.PageInterestDto;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.entities.Category;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.entities.UserInterest;
import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.CategoryRepository;
import sit.cp23ms2.sportconnect.repositories.UserInterestRepository;
import sit.cp23ms2.sportconnect.repositories.UserRepository;
import sit.cp23ms2.sportconnect.utils.AuthenticationUtil;
import sit.cp23ms2.sportconnect.utils.ListMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserInterestsService {

    @Autowired
    UserInterestRepository repository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ListMapper listMapper;
    @Autowired
    private AuthenticationUtil authenticationUtil;

    public PageInterestDto getAllUserInterests(int pageNum, int pageSize) {

        Pageable pageRequest = PageRequest.of(pageNum, pageSize);
        Page<UserInterest> listInterests = repository.findAll(pageRequest);
        return modelMapper.map(listInterests, PageInterestDto.class);
    }

    public UserInterest getById(Integer userId, Integer categoryId) {
//        UserInterest userInterest = repository.findByUser_UserIdAndCategory_CategoryId(userId, categoryId).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "Interest of User ID: " + userId + " and Category ID: "
//                        + categoryId + " Not Found"));
        UserInterest userInterest = repository.findByUser_UserIdAndCategory_CategoryId(userId, categoryId).orElseThrow(() ->
                new ApiNotFoundException("Interest of User ID: " + userId + " and Category ID: "
                        + categoryId + " Not Found"));
        return userInterest;
    }

    public UserInterest create(CreateInterestDto newInterest) throws ForbiddenException {
        if(!isCurrentGoingToOwnThisInterest(newInterest))
            throw new ForbiddenException("You're not allowed to create this Interest since you're not this user");
        User user = userRepository.findById(newInterest.getUserId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID: " + newInterest.getUserId() + " Not Found"));
        Category category = categoryRepository.findById(newInterest.getCategoryId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Category ID: " + newInterest.getCategoryId() + " Not Found"));
        UserInterest userInterest = modelMapper.map(newInterest, UserInterest.class);
        return repository.saveAndFlush(userInterest);
    }

//    public List<InterestDto> createBatch(CreateInterestBatchDto newInterest) throws ForbiddenException {
//        if(!isCurrentGoingToOwnThisInterest_(newInterest))
//            throw new ForbiddenException("You're not allowed to create this Interest since you're not this user");
//        User user = userRepository.findById(newInterest.getUserId()).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "User ID: " + newInterest.getUserId() + " Not Found"));
////        Category category = categoryRepository.findById(newInterest.getCategoryId()).orElseThrow(() ->
////                new ResponseStatusException(HttpStatus.NOT_FOUND, "Category ID: " + newInterest.getCategoryId() + " Not Found"));
//        List<UserInterest> userInterests = new ArrayList<>();
//        for (Integer categoryId : newInterest.getCategoryIds()) {
//            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "Category ID: " + categoryId + " Not Found"));
//            UserInterest interest = new UserInterest();
//            interest.setUser(user);
//            interest.setCategory(category);
//            userInterests.add(interest);
//        }
//        repository.saveAllAndFlush(userInterests);
//        return listMapper.mapList(userInterests, InterestDto.class, modelMapper);
//    }

    public List<InterestDto> createBatch(CreateInterestBatchDto newInterest) throws ForbiddenException {
        if(!isCurrentGoingToOwnThisInterest_(newInterest))
            throw new ForbiddenException("You're not allowed to create this Interest since you're not this user");
        User user = userRepository.findById(newInterest.getUserId()).orElseThrow(() ->
                new ApiNotFoundException("User ID: " + newInterest.getUserId() + " Not Found"));

        Set<Category> userInterests = new HashSet<>();
        List<UserInterest> userInterestsLists = new ArrayList<>();
        for(Integer categoryId : newInterest.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() ->
                        new ApiNotFoundException("Category ID: " + categoryId + " Not Found"));
            userInterests.add(category);
            UserInterest interest = new UserInterest();
            interest.setUser(user);
            interest.setCategory(category);
            userInterestsLists.add(interest);
        }
        user.setUserInterests(userInterests);
        userRepository.saveAndFlush(user);

        return listMapper.mapList(userInterestsLists, InterestDto.class, modelMapper);
    }

    public void delete(Integer userId, Set<Integer> categoryIds) throws ForbiddenException {
        for(Integer categoryId : categoryIds) {
            UserInterest userInterest = getById(userId, categoryId);
            if(!isCurrentUserHost(userInterest))
                throw new ForbiddenException("You're not allowed to delete other interest");
            repository.deleteByUser_UserIdAndCategory_CategoryId(userId, categoryId);
        }
    }

    private boolean isCurrentUserHost(UserInterest userInterest) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return userInterest.getUser().getUserId() == currentAuthId;

        }
    }
    private boolean isCurrentGoingToOwnThisInterest(CreateInterestDto createInterestDto) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return createInterestDto.getUserId() == currentAuthId;

        }
    }
    private boolean isCurrentGoingToOwnThisInterest_(CreateInterestBatchDto createInterestBatchDto) {
        if(authenticationUtil.getCurrentAuthenticationRole().equals("[ROLE_admin]")) {
            return true;
        } else {
            Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
            return createInterestBatchDto.getUserId() == currentAuthId;

        }
    }
}
