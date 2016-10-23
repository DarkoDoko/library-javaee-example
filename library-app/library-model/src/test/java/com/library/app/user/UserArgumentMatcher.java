package com.library.app.user;

import com.library.app.user.model.User;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

public class UserArgumentMatcher extends ArgumentMatcher<User> {

    private User expectedUser;

    public UserArgumentMatcher(User expectedUser) {
        this.expectedUser = expectedUser;
    }
    
    public static User userEq(User expectedUser) {
        return Matchers.argThat(new UserArgumentMatcher(expectedUser));
    }

    @Override
    public boolean matches(Object argument) {
        User actualUser = (User) argument;

        assertThat(actualUser.getId(), is(equalTo(expectedUser.getId())));
        assertThat(actualUser.getName(), is(equalTo(expectedUser.getName())));
        assertThat(actualUser.getEmail(), is(equalTo(expectedUser.getEmail())));
        assertThat(actualUser.getPassword(), is(equalTo(expectedUser.getPassword())));

        return true;
    }

}
