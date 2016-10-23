package com.library.app.user.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.PasswordUtils;
import static com.library.app.user.UserArgumentMatcher.userEq;
import com.library.app.user.UserExistentException;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import static com.library.app.user.UserForTestsRepository.userWithEncryptedPassword;
import static com.library.app.user.UserForTestsRepository.userWithIdAndCreatedAt;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.model.User;
import com.library.app.user.repository.UserRepository;
import com.library.app.user.services.UserServices;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class UserServicesTest {

    private Validator validator;
    private UserServices userServices;

    @Mock
    private UserRepository userRepository;

    @Before
    public void initTestCase() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        MockitoAnnotations.initMocks(this);

        userServices = new UserServicesImpl();
        ((UserServicesImpl) userServices).repository = userRepository;
        ((UserServicesImpl) userServices).validator = validator;
    }

    @Test
    public void addUserWithNullName() {
        User user = johnDoe();
        user.setName(null);
        addUserWithInvalidField(user, "name");
    }

    @Test
    public void addUserWithShortName() {
        final User user = johnDoe();
        user.setName("Jo");
        addUserWithInvalidField(user, "name");
    }

    @Test
    public void addUserWithNullEmail() {
        final User user = johnDoe();
        user.setEmail(null);
        addUserWithInvalidField(user, "email");
    }

    @Test
    public void addUserWithInvalidEmail() {
        final User user = johnDoe();
        user.setEmail("invalidemail");
        addUserWithInvalidField(user, "email");
    }

    @Test
    public void addUserWithNullPassword() {
        final User user = johnDoe();
        user.setPassword(null);
        addUserWithInvalidField(user, "password");
    }

    @Test(expected = UserExistentException.class)
    public void addExistentUser() {
        when(userRepository.alreadyExists(johnDoe())).thenThrow(new UserExistentException());

        userServices.add(johnDoe());
    }

    @Test
    public void addValidUser() {
        when(userRepository.alreadyExists(johnDoe())).thenReturn(false);
        when(userRepository.add(userEq(userWithEncryptedPassword(johnDoe()))))
                .thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        final User user = userServices.add(johnDoe());
        assertThat(user.getId(), is(equalTo(1L)));
    }

    @Test(expected = UserNotFoundException.class)
    public void findUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(null);

        userServices.findById(1L);
    }

    @Test
    public void findUserById() {
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        final User user = userServices.findById(1L);
        assertThat(user, is(notNullValue()));
        assertThat(user.getName(), is(equalTo(johnDoe().getName())));
    }

    @Test
    public void updateUserWithNullName() {
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        final User user = userWithIdAndCreatedAt(johnDoe(), 1L);
        user.setName(null);

        try {
            userServices.update(user);
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo("name")));
        }
    }

    @Test(expected = UserExistentException.class)
    public void updateUserExistent() throws Exception {
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        final User user = userWithIdAndCreatedAt(johnDoe(), 1L);
        when(userRepository.alreadyExists(user)).thenReturn(true);

        userServices.update(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void updateUserNotFound() throws Exception {
        final User user = userWithIdAndCreatedAt(johnDoe(), 1L);
        when(userRepository.findById(1L)).thenReturn(null);

        userServices.update(user);
    }

    @Test
    public void updateValidUser() throws Exception {
        final User user = userWithIdAndCreatedAt(johnDoe(), 1L);
        user.setPassword(null);
        when(userRepository.findById(1L)).thenReturn(userWithIdAndCreatedAt(johnDoe(), 1L));

        userServices.update(user);

        final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1L);
        verify(userRepository).update(userEq(expectedUser));
    }

    @Test(expected = UserNotFoundException.class)
    public void updatePasswordUserNotFound() {
        when(userRepository.findById(1L)).thenThrow(new UserNotFoundException());

        userServices.updatePassword(1L, "123456");
    }

    @Test
    public void updatePassword() throws Exception {
        final User user = userWithIdAndCreatedAt(johnDoe(), 1L);
        when(userRepository.findById(1L)).thenReturn(user);

        userServices.updatePassword(1L, "654654");

        final User expectedUser = userWithIdAndCreatedAt(johnDoe(), 1L);
        expectedUser.setPassword(PasswordUtils.encryptPassword("654654"));
        verify(userRepository).update(userEq(expectedUser));
    }

    private void addUserWithInvalidField(final User user, final String expectedInvalidFieldName) {
        try {
            userServices.add(user);
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo(expectedInvalidFieldName)));
        }
    }

}
