package sit.cp23ms2.sportconnect.tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sit.cp23ms2.sportconnect.entities.User;
import sit.cp23ms2.sportconnect.enums.Role;
import sit.cp23ms2.sportconnect.repositories.UserRepository;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader =  request.getHeader("Authorization");

        String jwtToken = null;
        String username = null;
        String bearerToken = request.getHeader("Authorization");
        //Cookie refreshCookie = WebUtils.getCookie(request, "refreshToken");
        //System.out.println("cookie: " + refreshCookie);
        String refreshToken = "";
//        if(refreshCookie != null){
//            refreshToken = refreshCookie.getValue();
//            System.out.println("token: " + refreshToken);
//        }
        try {
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                jwtToken = bearerToken.substring(7, bearerToken.length());
                DecodedJWT jwt = JWT.decode(jwtToken);
                username = jwtUtil.getEmailFromToken(jwtToken);
                //create if does not exist
//                if(!userRepository.existsByEmail(username)) {
//                    User user = new User();
//                    user.setEmail(username);
//                    user.setUsername(username);
//                    user.setRole(Role.valueOf("user"));
//                    userRepository.saveAndFlush(user);
//                }
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    System.out.println("loading");
                    CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(jwtToken, userDetails)) {
                        System.out.println("validated");
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(request);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } else {
                    System.out.println("none");
                }
            }
        } catch (Exception e) {

        }

        filterChain.doFilter(request, response);
    }
}
