package com.gestoteam.service;

import com.gestoteam.exception.GestoServiceException;
import com.gestoteam.model.User;
import com.gestoteam.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseService {

    protected final UserRepository userRepository;

    protected BaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    protected Long getCurrentUserId() {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GestoServiceException("Usuario no encontrado: " + username));
        return user.getId();
    }
}
