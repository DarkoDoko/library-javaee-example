package com.library.app.author.resource;

import com.library.app.author.model.AuthorFilter;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class AuthorFilterExtractorFromUrlTest {

    @Mock
    private UriInfo uriInfoCollaborator;

    @Before
    public void initTestCase() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void onlyDefaultValues() {
        setUpUriInfo(null, null, null, null);

        final AuthorFilterExtractorFromUrl extractorUnderTest = new AuthorFilterExtractorFromUrl(uriInfoCollaborator);
        final AuthorFilter authorFilter = extractorUnderTest.getFilter();

        assertActualPaginationDataWithExpected(authorFilter.getPaginationData(), new PaginationData(0, 10, "name",
                        OrderMode.ASCENDING));
        assertThat(authorFilter.getName(), is(nullValue()));
    }
    
    @Test
	public void withPaginationAndNameAndSortAscending() {
		setUpUriInfo("2", "5", "Robert", "id");

		AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfoCollaborator);
		AuthorFilter authorFilter = extractor.getFilter();

		assertActualPaginationDataWithExpected(authorFilter.getPaginationData(), new PaginationData(10, 5, "id",
				OrderMode.ASCENDING));
		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	@Test
	public void withPaginationAndNameAndSortAscendingWithPrefix() {
		setUpUriInfo("2", "5", "Robert", "+id");

		AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfoCollaborator);
		AuthorFilter authorFilter = extractor.getFilter();

		assertActualPaginationDataWithExpected(authorFilter.getPaginationData(), new PaginationData(10, 5, "id",
				OrderMode.ASCENDING));
		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

	@Test
	public void withPaginationAndNameAndSortDescending() {
		setUpUriInfo("2", "5", "Robert", "-id");

		AuthorFilterExtractorFromUrl extractor = new AuthorFilterExtractorFromUrl(uriInfoCollaborator);
		AuthorFilter authorFilter = extractor.getFilter();

		assertActualPaginationDataWithExpected(authorFilter.getPaginationData(), new PaginationData(10, 5, "id",
				OrderMode.DESCENDING));
		assertThat(authorFilter.getName(), is(equalTo("Robert")));
	}

    private void setUpUriInfo(String page, String perPage, String name, String sort) {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("name", name);
        parameters.put("sort", sort);

        MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);

        parameters.entrySet().forEach((keyValue) -> {
            when(multiMap.getFirst(keyValue.getKey())).thenReturn(keyValue.getValue());
        });

        when(uriInfoCollaborator.getQueryParameters()).thenReturn(multiMap);
    }
    
    private static void assertActualPaginationDataWithExpected(PaginationData actual, PaginationData expected) {
        assertThat(actual.getFirstResult(), is(equalTo(expected.getFirstResult())));
        assertThat(actual.getMaxResults(), is(equalTo(expected.getMaxResults())));
        assertThat(actual.getOrderField(), is(equalTo(expected.getOrderField())));
        assertThat(actual.getOrderMode(), is(equalTo(expected.getOrderMode())));
    }
}
