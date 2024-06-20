package com.aruba.Orchestrator.service.impl;


import com.aruba.Orchestrator.entity.User;
import com.aruba.Orchestrator.repository.UserRepository;
import com.aruba.Orchestrator.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Base64;
import java.util.concurrent.ExecutionException;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public User findById(Long id){

        try {
        User u = userRepository.findById(id).toFuture().get();
        if (u == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Enable to found user with id: " + id);

        }
        return u;
        } catch (InterruptedException | ExecutionException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @Override
    public User findByUsername(String username){

        try {
            User u = userRepository.findByUsername(username).toFuture().get();
            if (u == null) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Enable to found user with username: " + username);
            }
            return u;
        } catch (InterruptedException | ExecutionException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

    @Override
    public User findByEmail(String email){

        try {
            User u = userRepository.findByEmail(email).toFuture().get();
            if (u == null) {
                throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Enable to found user with email: " + email);
            }
            return u;
        } catch (InterruptedException | ExecutionException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }

    }

    @Override
    public User getLoggedUserByUsername(String token){

        String field = this.getUsernameFromToken(token);
        if (field == null) throw new RuntimeException("Malformed Access token.");
        return this.findByUsername(field);
    }

    @Override
    public User getLoggedUserByEmail(String token){

        String field = this.getEmailFromToken(token);
        if (field == null) throw new RuntimeException("Malformed Access token.");
        return this.findByEmail(field);
    }

    private String getEmailFromToken(String token) {

        try {
            String[] splitted = token.split("\\.");
            if (splitted.length == 3) {
                String decoded = new String(Base64.getDecoder().decode(splitted[1]));
                JsonNode node = objectMapper.readTree(decoded);
                return node.get("email").asText();
            }
        } catch (JsonProcessingException ignored) {
        }

        return null;
    }

    private String getUsernameFromToken(String token) {

        try {
            String[] splitted = token.split("\\.");
            if (splitted.length == 3) {
                String decoded = new String(Base64.getDecoder().decode(splitted[1]));
                JsonNode node = objectMapper.readTree(decoded);
                return node.get("preferred_username").asText();
            }
        } catch (JsonProcessingException ignored) {
        }

        return null;
    }


}
