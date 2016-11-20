package com.library.app.user.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.PasswordUtils;
import com.library.app.ValidationUtils;
import com.library.app.pagination.PaginatedData;
import com.library.app.user.UserExistentException;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.model.filter.UserFilter;
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

    @Override
    public User findById(Long id) {
        final User user = repository.findById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public void update(User user) {
        User existentUser = findById(user.getId());
        user.setPassword(existentUser.getPassword());

        validateUser(user);

        repository.update(user);
    }

    @Override
    public void updatePassword(Long id, String password) {
        User user = findById(id);
        user.setPassword(PasswordUtils.encryptPassword(password));

        repository.update(user);
    }

    @Override
    public User findByEmail(String email) throws UserNotFoundException {
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    @Override
    public User findByEmailAndPassword(final String email, final String password) {
        final User user = findByEmail(email);

        if (!user.getPassword().equals(PasswordUtils.encryptPassword(password))) {
            throw new UserNotFoundException();
        }

        return user;
    }

    @Override
    public PaginatedData<User> findByFilter(final UserFilter userFilter) {
        return repository.findByFilter(userFilter);
    }

    private void validateUser(User user) throws FieldNotValidException, UserExistentException{
        if (repository.alreadyExists(user)) {
            throw new UserExistentException();
        }

        ValidationUtils.validateEntityFields(validator, user);
    }

}
