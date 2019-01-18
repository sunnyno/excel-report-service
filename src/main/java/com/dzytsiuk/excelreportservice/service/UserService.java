package com.dzytsiuk.excelreportservice.service;

import com.dzytsiuk.excelreportservice.entity.User;

import java.util.List;

public interface UserService {
    List<User> getTopUsers();
}
