package sit.cp23ms2.sportconnect.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sit.cp23ms2.sportconnect.dtos.user.UserDto;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.services.UserService;
import sit.cp23ms2.sportconnect.tokens.JwtUtil;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
@Component
public class AuthController {
    @Autowired
    public UserService userService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String jwtToken = bearerToken.substring(7, bearerToken.length());
            String email = jwtUtil.getEmailFromToken(jwtToken);
            User user = userService.getByEmail(email);
            UserDto mapped = modelMapper.map(user, UserDto.class);
            return new ResponseEntity<>(mapped, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
