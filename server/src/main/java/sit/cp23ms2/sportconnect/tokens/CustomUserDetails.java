package sit.cp23ms2.sportconnect.tokens;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Integer userId;  // เพิ่ม custom field ตรงนี้

    public CustomUserDetails(String email, String password, Collection<? extends GrantedAuthority> authorities, Integer userId) {
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.userId = userId;
    }

    // Implement methods from UserDetails interface

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // เพิ่มเมทอด getter สำหรับ customField

    public Integer getUserId() {
        return userId;
    }

    // Implement methods from UserDetails interface
}
