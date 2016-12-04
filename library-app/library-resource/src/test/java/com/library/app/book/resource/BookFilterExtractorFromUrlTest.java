package com.library.app.book.resource;

import com.library.app.book.model.BookFilter;
import static com.library.app.commontests.utils.FilterExtractorTestUtils.assertActualPaginationDataWithExpected;
import static com.library.app.commontests.utils.FilterExtractorTestUtils.setupUriInfoWithMap;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookFilterExtractorFromUrlTest {

    @Mock
    private UriInfo uriInfo;
    
    @Before
	public void initTestCase() {
		MockitoAnnotations.initMocks(this);
	}

    @Test
    public void onlyDefaultValues() {
        setUpUriInfo(null, null, null, null, null);

        BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(0, 10, "title",
            OrderMode.ASCENDING));
        assertFieldsOnFilter(bookFilter, null, null);
    }

    @Test
    public void withPaginationAndTitleAndCategoryIdAndSortAscending() {
        setUpUriInfo("2", "5", "Design", "1", "id");

        final BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        final BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(10, 5, "id",
            OrderMode.ASCENDING));
        assertFieldsOnFilter(bookFilter, "Design", 1L);
    }

    @Test
    public void withPaginationAndTitleAndSortAscendingWithPrefix() {
        setUpUriInfo("2", "5", "Design", null, "+id");

        final BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        final BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(10, 5, "id",
            OrderMode.ASCENDING));
        assertFieldsOnFilter(bookFilter, "Design", null);
    }

    @Test
    public void withPaginationAndTitleAndCategoryIdAndSortDescending() {
        setUpUriInfo("2", "5", "Design", "10", "-id");

        final BookFilterExtractorFromUrl extractor = new BookFilterExtractorFromUrl(uriInfo);
        final BookFilter bookFilter = extractor.getFilter();

        assertActualPaginationDataWithExpected(bookFilter.getPaginationData(), new PaginationData(10, 5, "id",
            OrderMode.DESCENDING));
        assertFieldsOnFilter(bookFilter, "Design", 10L);
    }

    private void setUpUriInfo(final String page, final String perPage, final String title, final String categoryId,
        final String sort) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("page", page);
        parameters.put("per_page", perPage);
        parameters.put("title", title);
        parameters.put("categoryId", categoryId);
        parameters.put("sort", sort);

        setupUriInfoWithMap(uriInfo, parameters);
    }

    private void assertFieldsOnFilter(final BookFilter bookFilter, final String title, final Long categoryId) {
        assertThat(bookFilter.getTitle(), is(equalTo(title)));
        assertThat(bookFilter.getCategoryId(), is(equalTo(categoryId)));
    }
}
