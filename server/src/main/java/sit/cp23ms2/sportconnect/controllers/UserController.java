package sit.cp23ms2.sportconnect.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import sit.cp23ms2.sportconnect.dtos.activity.ActivityDto;
import sit.cp23ms2.sportconnect.dtos.activity.CreateActivityDto;
import sit.cp23ms2.sportconnect.dtos.user.CreateUserDto;
import sit.cp23ms2.sportconnect.dtos.user.PageUserDto;
import sit.cp23ms2.sportconnect.dtos.user.UpdateUserDto;
import sit.cp23ms2.sportconnect.dtos.user.UserDto;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.exceptions.type.ForbiddenException;
import sit.cp23ms2.sportconnect.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;


import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/users")
@Component
public class UserController {
    @Autowired
    public UserService userService;
    @Autowired
    ModelMapper modelMapper;
    //@Value("${line.notify.client}")
    private String clientLine;
    //@Value("${line.notify.secret}")
    private String secretLine;

    @GetMapping
    public PageUserDto getUsers(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int pageSize,
                                @RequestParam(defaultValue = "username") String sortBy,
                                @RequestParam(required = false) String email) throws IOException {
        //response.sendRedirect("https://google.com");

        return userService.getUser(page, pageSize, sortBy, email);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Integer id) {
        return modelMapper.map(userService.getById(id), UserDto.class);
    }

    @PostMapping
    public UserDto createUser(@Valid @ModelAttribute CreateUserDto newUser, BindingResult result) throws MethodArgumentNotValidException {
        return userService.create(newUser, result);
    }

    @PatchMapping("/{id}")
    public UserDto update(@Valid @ModelAttribute UpdateUserDto updateUserDto, @PathVariable Integer id) throws ForbiddenException {
        return userService.update(updateUserDto, id);
    }

    @GetMapping("/us")
    public PageUserDto getUser(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int pageSize,
                               @RequestParam(defaultValue = "username") String sortBy,
                               @RequestParam(required = false) String email) {
        return userService.getUser(page, pageSize, sortBy, email);
    }

    @PostMapping("/test")
    public ResponseEntity testCall(@RequestParam String code) {


        String uri = "https://notify-bot.line.me/oauth/token" +
                "?grant_type=authorization_code&" +
                "code=" + code + "&" +
                "redirect_uri=http://localhost:3000&" +
                "client_id=" + clientLine + "&" +
                "client_secret=" + secretLine;
        String uri2 = "http://localhost:8080/api/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN );
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        return restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("grant_type", "authorization_code");
//        params.add("code", "roNy5T9MI3ZnpRjzCN6WUx");
//        params.add("redirect_uri", "http://localhost:3000");
//        params.add("client_id", "eOnn6OgbERPraaI5IkyW0Q");
//        params.add("client_secret", "ac6uH4EA6ep5FpUGHnfQkkcJv9BpB9UtoLU0rbQ7Sln");
    }
//    @GetMapping("/userz")
//    public Object user(Principal principal) {
//        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    }

}
