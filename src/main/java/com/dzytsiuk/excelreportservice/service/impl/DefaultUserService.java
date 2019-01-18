package com.dzytsiuk.excelreportservice.service.impl;

import com.dzytsiuk.excelreportservice.entity.User;
import com.dzytsiuk.excelreportservice.service.UserService;
import com.dzytsiuk.excelreportservice.service.impl.client.MovielandClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultUserService implements UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MovielandClient movielandClient;

    public DefaultUserService(MovielandClient movielandClient) {
        this.movielandClient = movielandClient;
    }

    public List<User> fallback() {
        return new ArrayList<>();
    }

    @HystrixCommand(fallbackMethod = "fallback")
    @Override
    public List<User> getTopUsers() {
        log.info("Sending request to get top users");
        List<User> users = movielandClient.getUsers();
        log.info("Users {} received", users);
        return users;
    }
}
