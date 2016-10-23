package com.library.app.user.services;

import com.library.app.FieldNotValidException;
import com.library.app.user.UserExistentException;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.User;
import javax.ejb.Local;

@Local
public interface UserServices {
    
    User add(User user) throws FieldNotValidException, UserExistentException;
    
    User findById(Long id) throws UserNotFoundException;
    
    void update(User user) throws FieldNotValidException, UserExistentException, UserNotFoundException;
    
    void updatePassword(Long id, String password) throws UserNotFoundException;
    
    User findByEmail(String email) throws UserNotFoundException;
    
    User findByEmailAndPassword(String email, String password) throws UserNotFoundException;
    
}
