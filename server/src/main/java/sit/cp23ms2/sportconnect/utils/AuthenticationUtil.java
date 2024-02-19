package sit.cp23ms2.sportconnect.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import sit.cp23ms2.sportconnect.tokens.CustomUserDetails;

public class AuthenticationUtil {
    public String getCurrentAuthenticationEmail() {
        CustomUserDetails currentAuthenticationDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentAuthenticationDetails.getUsername();
    }

    public Integer getCurrentAuthenticationUserId() {
        CustomUserDetails currentAuthenticationDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentAuthenticationDetails.getUserId();
    }

    public String getCurrentAuthenticationRole() {
        CustomUserDetails currentAuthenticationDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentAuthenticationDetails.getAuthorities().toString();
    }
}
