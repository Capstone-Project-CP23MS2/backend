package sit.cp23ms2.sportconnect.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import sit.cp23ms2.sportconnect.dtos.user_interests.CreateInterestBatchDto;
import sit.cp23ms2.sportconnect.dtos.user_interests.CreateInterestDto;
import sit.cp23ms2.sportconnect.dtos.user_interests.InterestDto;
import sit.cp23ms2.sportconnect.dtos.user_interests.PageInterestDto;
import sit.cp23ms2.sportconnect.services.UserInterestsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/interests")
@Component
public class UserInterestsController {
    @Autowired
    UserInterestsService userInterestsService;
    @Autowired
    ModelMapper modelMapper;

    @GetMapping
    public PageInterestDto getInterests(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int pageSize) {
        return userInterestsService.getAllUserInterests(page, pageSize);
    }

    @GetMapping("/{userId}/{categoryId}")
    public InterestDto getById(@PathVariable Integer userId, @PathVariable Integer categoryId) {
        return modelMapper.map(userInterestsService.getById(userId, categoryId), InterestDto.class);
    }

    @PostMapping
    public InterestDto createInterest(@Valid @ModelAttribute CreateInterestDto createInterestDto) {
        return modelMapper.map(userInterestsService.create(createInterestDto), InterestDto.class);
    }

    @PostMapping("/create")
    public List<InterestDto> createInterest(@Valid @ModelAttribute CreateInterestBatchDto createInterestDto) {
        return userInterestsService.createBatch(createInterestDto);
    }

    @DeleteMapping("/{userId}/{categoryId}")
    public void delete(@PathVariable Integer userId, @PathVariable Integer categoryId) {
        userInterestsService.delete(userId, categoryId);
    }
}
