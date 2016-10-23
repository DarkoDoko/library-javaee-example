package com.library.app.user.services.impl;

import com.library.app.FieldNotValidException;
import static com.library.app.user.UserForTestsRepository.johnDoe;
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

    private void addUserWithInvalidField(final User user, final String expectedInvalidFieldName) {
        try {
            userServices.add(user);
            fail("An error should have been thrown");
        } catch (final FieldNotValidException e) {
            assertThat(e.getFieldName(), is(equalTo(expectedInvalidFieldName)));
        }
    }

}
