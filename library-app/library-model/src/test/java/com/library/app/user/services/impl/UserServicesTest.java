package com.library.app.user.services.impl;

import com.library.app.FieldNotValidException;
import com.library.app.user.UserExistentException;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import static com.library.app.user.UserForTestsRepository.userWithEncryptedPassword;
import static com.library.app.user.UserForTestsRepository.userWithIdAndCreatedAt;
import com.library.app.user.model.User;
import com.library.app.user.repository.UserRepository;
import com.library.app.user.services.UserServices;
import javax.validation.Validation;
import javax.validation.Validator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
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

    private void addUserWithInvalidField(final User user, final String expectedInvalidFieldName) {
        try {
            userServices.add(user);
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo(expectedInvalidFieldName)));
        }
    }

}
