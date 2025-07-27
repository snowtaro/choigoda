package com.choigoda.choigoda.service;

import com.choigoda.choigoda.dto.HomeResponseDto;
import com.choigoda.choigoda.entity.User;
import com.choigoda.choigoda.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class HomeServiceImpl implements HomeService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public HomeResponseDto getHomeInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
        return new HomeResponseDto(username, "안녕하세요");
    }
}
