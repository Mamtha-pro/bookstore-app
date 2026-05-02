package com.bookstore.mapper;

import com.bookstore.dto.response.UserResponse;
import com.bookstore.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole().name());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }
}