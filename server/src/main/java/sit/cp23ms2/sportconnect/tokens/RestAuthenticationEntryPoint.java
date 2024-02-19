package sit.cp23ms2.sportconnect.tokens;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        final String expired = (String) request.getAttribute("expired");
        System.out.println(request.getHeader("auth"));
        //System.out.println(response);
        System.out.println("expired: " + expired);
        String authHeader = request.getHeader("auth");
//        if(authHeader != null && !authHeader.isEmpty()) {
//            if(jwtUtil.isTokenExpireOrNot(authHeader.substring(7))) {
//                response.setContentType("application/json");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getOutputStream().println("{ \"error\": \"" + "Token has expired" + "\" }");
//            }
//        } else {
//            response.setContentType("application/json");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getOutputStream().println("{ \"error\": \"" + "Invalid or No Token" + "\" }");
//        }
        if(expired!=null) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println("{ \"error\": \"" + expired + "\" }");
        } else {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().println("{ \"error\": \"" + "Invalid or Expired Token" + "\" }");
//            response.getOutputStream().println("{ \"error\": \"" + authException.getMessage() + "\" }");
        }
    }

}
