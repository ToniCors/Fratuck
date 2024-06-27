package com.aruba.Inventory.service.impl;

import com.aruba.Inventory.service.UserService;
import com.aruba.Lib.entity.User;
import com.aruba.Lib.exception.ApiException;
import com.aruba.Lib.exception.ErrorCodes;
import com.aruba.Lib.exception.ResponseError;
import com.aruba.Lib.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    public User findById(Long id) {

        Optional<User> res = repository.findById(id);

        if (res.isEmpty()) {
            throw new ApiException(ResponseError.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(String.format("User with id {%s} was not found.", id))
                    .errorCodes(ErrorCodes.ENTITY_NOT_FOUND)
                    .build());
        }

        return res.get();
    }
}
