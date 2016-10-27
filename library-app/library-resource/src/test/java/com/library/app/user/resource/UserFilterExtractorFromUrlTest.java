package com.library.app.user.resource;

import static com.library.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.library.app.commontests.utils.FilterExtractorTestUtils.setupUriInfoWithMap;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import com.library.app.user.model.User.UserType;
import com.library.app.user.model.filter.UserFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.mock;

public class UserFilterExtractorFromUrlTest {

    private UriInfo uriInfo;

    @Before
    public void initTestCase() {
        uriInfo = mock(UriInfo.class);
    }

    @Test
    public void onlyDefaultValues() {
        setUpUriInfo(null, null, null, null, null);

        UserFilterExtractorFromUrl extractorUnderTest = new UserFilterExtractorFromUrl(uriInfo);
        UserFilter userFilter = extractorUnderTest.getFilter();

        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(0, 10, "name",
            OrderMode.ASCENDING));
        assertFieldsOnFilter(userFilter, null, null);
    }

    @Test
    public void withPaginationAndNameAndUserTypeAndSortAscending() {
        setUpUriInfo("2", "5", "John", UserType.CUSTOMER, "id");

        UserFilterExtractorFromUrl extractor = new UserFilterExtractorFromUrl(uriInfo);
        UserFilter userFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(10, 5, "id",
            OrderMode.ASCENDING));
        assertFieldsOnFilter(userFilter, "John", UserType.CUSTOMER);
    }

    @Test
    public void withPaginationAndNameAndSortAscendingWithPrefix() {
        setUpUriInfo("2", "5", "John", null, "+id");

        UserFilterExtractorFromUrl extractor = new UserFilterExtractorFromUrl(uriInfo);
        UserFilter userFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(10, 5, "id",
            OrderMode.ASCENDING));
        assertFieldsOnFilter(userFilter, "John", null);
    }

    @Test
    public void withPaginationAndNameeAndUserTypeAndSortDescending() {
        setUpUriInfo("2", "5", "John", UserType.EMPLOYEE, "-id");

        UserFilterExtractorFromUrl extractor = new UserFilterExtractorFromUrl(uriInfo);
        UserFilter userFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(userFilter.getPaginationData(), new PaginationData(10, 5, "id",
            OrderMode.DESCENDING));
        assertFieldsOnFilter(userFilter, "John", UserType.EMPLOYEE);
    }

    private void setUpUriInfo(String page, String perPage, String name, UserType type, String sort) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("name", name);
        parameters.put("type", type != null ? type.name() : null);
        parameters.put("sort", sort);

        setupUriInfoWithMap(uriInfo, parameters);
    }

    private void assertFieldsOnFilter(UserFilter userFilter, String name, UserType userType) {
        assertThat(userFilter.getName(), is(equalTo(name)));
        assertThat(userFilter.getUserType(), is(equalTo(userType)));
    }

}
