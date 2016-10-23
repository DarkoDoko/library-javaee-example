package com.library.app.user.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.PasswordUtils;
import com.library.app.ValidationUtils;
import com.library.app.user.UserExistentException;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.repository.UserRepository;
import com.library.app.user.services.UserServices;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Validator;

@Stateless
public class UserServicesImpl implements UserServices {

    @Inject
    UserRepository repository;

    @Inject
    Validator validator;

    @Override
    public User add(User user) {
        validateUser(user);
        user.setPassword(PasswordUtils.encryptPassword(user.getPassword()));

        return repository.add(user);
    }

    private void validateUser(User user) throws FieldNotValidException {
        if (repository.alreadyExists(user)) {
            throw new UserExistentException();
        }

        ValidationUtils.validateEntityFields(validator, user);
    }

    @Override
    public User findById(final Long id) {
        final User user = repository.findById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

}
