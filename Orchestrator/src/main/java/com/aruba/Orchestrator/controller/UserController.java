package com.aruba.Orchestrator.controller;

import com.aruba.Orchestrator.component.OrchestratorLogger;
import com.aruba.Orchestrator.entity.User;
import com.aruba.Orchestrator.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/users", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        OrchestratorLogger.logger.info("Find user by id: {}", id);

        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping(path = "/username/{username}")
    public ResponseEntity<User> getByUsername(@PathVariable String username) {
        OrchestratorLogger.logger.info("Find user by username: {}", username);

        return ResponseEntity.status(HttpStatus.OK).body(userService.findByUsername(username));
    }

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        OrchestratorLogger.logger.info("Find user by email: {}", email);
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByEmail(email));
    }

}
