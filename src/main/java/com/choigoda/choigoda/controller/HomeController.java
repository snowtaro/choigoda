package com.choigoda.choigoda.controller;

import com.choigoda.choigoda.dto.HomeResponseDto;
import com.choigoda.choigoda.entity.User;
import com.choigoda.choigoda.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @GetMapping
    public HomeResponseDto home(){
        return homeService.getHomeInfo();
    }
}
