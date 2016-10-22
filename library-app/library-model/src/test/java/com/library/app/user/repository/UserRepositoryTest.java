package com.library.app.user.repository;

import com.library.app.commontests.utils.TestBaseRepository;
import com.library.app.user.UserForTestsRepository;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import com.library.app.user.model.User;
import com.library.app.user.model.User.UserType;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class UserRepositoryTest extends TestBaseRepository{
    
    private UserRepository repositoryUnderTest;
    
    @Before
    public void initTestCase() {
        initializeTestDB();
        
        repositoryUnderTest = new UserRepository();
        repositoryUnderTest.em = em;
    }
    
    @After
    public void setDownTestCase() {
        closeEntityManager();
    }

    @Test
    public void addCustomerAndFindIt() {
        Long userAddedId = dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(johnDoe()).getId();
        });
        assertThat(userAddedId, is(notNullValue()));
        
        User user = repositoryUnderTest.findById(userAddedId);
        assertUser(user, johnDoe(), UserType.CUSTOMER);
    }
    
    @Test
    public void findUseryIdNotFound() {
        User user = repositoryUnderTest.findById(999L);
        assertThat(user, is(nullValue()));
    }
    
    @Test
    public void updateCustomer() {
        Long userAddedId = dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(johnDoe()).getId();
        });
        assertThat(userAddedId, is(notNullValue()));

        User user = repositoryUnderTest.findById(userAddedId);
        assertThat(user.getName(), is(equalTo(johnDoe().getName())));

        user.setName("New name");
        dbExecutor.executeCommand(() -> {
            repositoryUnderTest.update(user);
            return null;
        });

        final User userAfterUpdate = repositoryUnderTest.findById(userAddedId);
        assertThat(userAfterUpdate.getName(), is(equalTo("New name")));
    }
    
    private void assertUser(final User actualUser, final User expectedUser, final UserType expectedUserType) {
        assertThat(actualUser.getName(), is(equalTo(expectedUser.getName())));
        assertThat(actualUser.getEmail(), is(equalTo(expectedUser.getEmail())));
        assertThat(actualUser.getRoles().toArray(), is(equalTo(expectedUser.getRoles().toArray())));
        assertThat(actualUser.getCreatedAt(), is(notNullValue()));
        assertThat(actualUser.getPassword(), is(expectedUser.getPassword()));
        assertThat(actualUser.getUserType(), is(equalTo(expectedUserType)));
    }
    
}
