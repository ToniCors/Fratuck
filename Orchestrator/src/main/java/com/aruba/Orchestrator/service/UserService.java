package com.aruba.Orchestrator.service;

import com.aruba.Orchestrator.entity.User;

public interface UserService {
    User findById(Long id);

    User findByUsername(String useername);

    User findByEmail(String email);

    User getLoggedUserByUsername(String token);

    User getLoggedUserByEmail(String token);
}
