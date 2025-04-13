package com.thaihoc.miniinsta.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.thaihoc.miniinsta.dto.UserPrincipal;
import com.thaihoc.miniinsta.service.user.UserService;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {
    final private UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.thaihoc.miniinsta.model.User user = this.userService.getUserByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username/password không hợp lệ");
        }
        return UserPrincipal.create(user);
    }
}
