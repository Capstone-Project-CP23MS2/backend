package sit.cp23ms2.sportconnect.services;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import sit.cp23ms2.sportconnect.dtos.activity.ActivityDto;
import sit.cp23ms2.sportconnect.dtos.activity.CreateActivityDto;
import sit.cp23ms2.sportconnect.dtos.user.CreateUserDto;
import sit.cp23ms2.sportconnect.dtos.user.PageUserDto;
import sit.cp23ms2.sportconnect.dtos.user.UpdateUserDto;
import sit.cp23ms2.sportconnect.dtos.user.UserDto;
import sit.cp23ms2.sportconnect.entities.Activity;
import sit.cp23ms2.sportconnect.entities.ActivityParticipant;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.enums.Gender;
import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;



@Service
public class UserService {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository repository;
//    @Autowired
//    private AuthenticationUtil authenticationUtil;

    //nameError
    final private FieldError nameErrorObj = new FieldError("createUserDto",
            "username", "Username already used");

    public PageUserDto getUser(int pageNum, int pageSize, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page<User> listUsers = repository.findAllUsers(pageRequest); //ได้เป็น Pageable ของ User
        PageUserDto pageUserDto = modelMapper.map(listUsers, PageUserDto.class); //map ใส่ PageUserDto
        return  pageUserDto;
    }

    public User getById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ApiNotFoundException("User not found!"));
    }

    public UserDto create(CreateUserDto newUser, BindingResult result) throws MethodArgumentNotValidException {
        //Error validation
        if(repository.existsByUsername(newUser.getUsername())) {
            result.addError(nameErrorObj);
        }
        if (result.hasErrors()) throw new MethodArgumentNotValidException(null, result);

        User user = modelMapper.map(newUser, User.class);
        //save User to database
        User createdUser = repository.saveAndFlush(user);
        return modelMapper.map(createdUser, UserDto.class);
    }

    public UserDto update(UpdateUserDto updateUserDto, Integer id) throws ForbiddenException {
        User user = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User " + id + " does not exist !"));
//        if(!isCurrentUserHost(user))
//            throw new ForbiddenException("You're not allowed to edit this user");
        User updatedUser = mapUser(user, updateUserDto);
        return modelMapper.map(repository.saveAndFlush(updatedUser), UserDto.class);
    }

    private User mapUser(User existingUser, UpdateUserDto updateUserDto) {
        if(updateUserDto.getUsername() != null && !updateUserDto.getUsername().trim().equals("")) {
            existingUser.setUsername(updateUserDto.getUsername());
        }
        if(updateUserDto.getGender() != null) {
            existingUser.setGender(Gender.valueOf(updateUserDto.getGender()));
        }
        if(updateUserDto.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserDto.getDateOfBirth());
        }
        if(updateUserDto.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateUserDto.getPhoneNumber());
        }
        if(updateUserDto.getLineId() != null) {
            existingUser.setLineId(updateUserDto.getLineId());
        }
        return existingUser;
    }

//    private boolean isCurrentUserHost(User user) {
//        Integer currentAuthId = authenticationUtil.getCurrentAuthenticationUserId();
//        return user.getUserId() == currentAuthId;
//    }
}
