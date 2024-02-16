//package sit.cp23ms2.sportconnect.tokens;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import sit.cp23ms2.sportconnect.exceptions.type.ApiNotFoundException;
//import sit.cp23ms2.sportconnect.repositories.UserRepository;
//
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    UserRepository jwtUserRepository;
//
//    @Override
//    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        if(jwtUserRepository.existsByEmail(email)){
//            sit.cp23ms2.sportconnect.entities.User jwtUser = jwtUserRepository.findByEmail(email);
//            return new CustomUserDetails(jwtUser.getEmail(), "test", getAuthority(jwtUser), jwtUser.getUserId());
//        } else {
//            System.out.println("email does not exist");
//            throw new ApiNotFoundException("Email does not exist");
//        }
//
//
////        if (jwtUser == null) {
////            throw new UsernameNotFoundException("email Not found" + email);
////        }
//
//    }
//
//    private Set<SimpleGrantedAuthority> getAuthority(sit.cp23ms2.sportconnect.entities.User user) {
//        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//        //authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
//        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
//        return authorities;
//    }
//
//}
