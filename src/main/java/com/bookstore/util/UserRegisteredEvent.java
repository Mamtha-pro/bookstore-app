package com.bookstore.util;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final String email;
    private final String name;

    public UserRegisteredEvent(Object source, String email, String name) {
        super(source);
        this.email = email;
        this.name  = name;
    }
}