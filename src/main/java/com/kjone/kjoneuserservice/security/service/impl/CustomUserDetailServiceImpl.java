package com.kjone.kjoneuserservice.security.service.impl;



import com.kjone.kjoneuserservice.domain.user.User;
import com.kjone.kjoneuserservice.domain.user.UserForSecurity;
import com.kjone.kjoneuserservice.repository.UserRepository;
import com.kjone.kjoneuserservice.security.service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailServiceImpl implements CustomUserDetailService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));
        return UserForSecurity.builder()
                .user(user)
                .build();
    }

}

