package com.library.app.user.repository;

import com.library.app.commontests.utils.TestBaseRepository;
import com.library.app.pagination.PaginatedData;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import com.library.app.user.UserForTestsRepository;
import static com.library.app.user.UserForTestsRepository.admin;
import static com.library.app.user.UserForTestsRepository.allUsers;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import static com.library.app.user.UserForTestsRepository.mary;
import com.library.app.user.model.User;
import com.library.app.user.model.User.UserType;
import com.library.app.user.model.filter.UserFilter;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class UserRepositoryTest extends TestBaseRepository {

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

    @Test
    public void alreadyExistsUserWithoutId() {
        dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(johnDoe()).getId();
        });

        assertThat(repositoryUnderTest.alreadyExists(johnDoe()), is(equalTo(true)));
        assertThat(repositoryUnderTest.alreadyExists(admin()), is(equalTo(false)));
    }

    @Test
    public void alreadyExistsUserWithId() {
        final User customer = dbExecutor.executeCommand(() -> {
            repositoryUnderTest.add(admin());
            return repositoryUnderTest.add(johnDoe());
        });

        assertFalse(repositoryUnderTest.alreadyExists(customer));

        customer.setEmail(admin().getEmail());
        assertThat(repositoryUnderTest.alreadyExists(customer), is(equalTo(true)));

        customer.setEmail("newemail@domain.com");
        assertThat(repositoryUnderTest.alreadyExists(customer), is(equalTo(false)));
    }

    @Test
    public void findUserByEmail() {
        dbExecutor.executeCommand(() -> {
            return repositoryUnderTest.add(johnDoe());
        });

        User user = repositoryUnderTest.findByEmail(johnDoe().getEmail());
        assertUser(user, johnDoe(), UserType.CUSTOMER);
    }

    @Test
    public void findUserByEmailNotFound() {
        User user = repositoryUnderTest.findByEmail(johnDoe().getEmail());
        assertThat(user, is(nullValue()));
    }

    @Test
    public void findByFilterWithPagingOrderingByNameDescending() {
        loadDataForFindByFilter();

        UserFilter userFilter = new UserFilter();
        userFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.DESCENDING));

        PaginatedData<User> result = repositoryUnderTest.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getName(), is(equalTo(mary().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(johnDoe().getName())));

        userFilter = new UserFilter();
        userFilter.setPaginationData(new PaginationData(2, 2, "name", OrderMode.DESCENDING));

        result = repositoryUnderTest.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(3)));
        assertThat(result.getRows().size(), is(equalTo(1)));
        assertThat(result.getRow(0).getName(), is(equalTo(admin().getName())));
    }

    @Test
    public void findByFilterFilteringByName() {
        loadDataForFindByFilter();

        UserFilter userFilter = new UserFilter();
        userFilter.setName("m");
        userFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.ASCENDING));

        PaginatedData<User> result = repositoryUnderTest.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(2)));
        assertThat(result.getRows().size(), is(equalTo(2)));
        assertThat(result.getRow(0).getName(), is(equalTo(admin().getName())));
        assertThat(result.getRow(1).getName(), is(equalTo(mary().getName())));
    }

    @Test
    public void findByFilterFilteringByNameAndType() {
        loadDataForFindByFilter();

        UserFilter userFilter = new UserFilter();
        userFilter.setName("m");
        userFilter.setUserType(UserType.EMPLOYEE);
        userFilter.setPaginationData(new PaginationData(0, 2, "name", OrderMode.ASCENDING));

        PaginatedData<User> result = repositoryUnderTest.findByFilter(userFilter);
        assertThat(result.getNumberOfRows(), is(equalTo(1)));
        assertThat(result.getRows().size(), is(equalTo(1)));
        assertThat(result.getRow(0).getName(), is(equalTo(admin().getName())));
    }
    
    private void loadDataForFindByFilter() {
        dbExecutor.executeCommand(() -> {
            allUsers().forEach(repositoryUnderTest::add);
            return null;
        });
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
