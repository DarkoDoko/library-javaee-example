package com.library.app.commontests.utils;

import com.library.app.pagination.filter.PaginationData;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public final class FilterExtractorTestUtils {

    private FilterExtractorTestUtils() {
    }
    
    public static void assertActualPaginationDataWithExpected(PaginationData actual, PaginationData expected) {
        assertThat(actual.getFirstResult(), is(equalTo(expected.getFirstResult())));
        assertThat(actual.getMaxResults(), is(equalTo(expected.getMaxResults())));
        assertThat(actual.getOrderField(), is(equalTo(expected.getOrderField())));
        assertThat(actual.getOrderMode(), is(equalTo(expected.getOrderMode())));
    }
    
    public static void setupUriInfoWithMap(UriInfo uriInfo, Map<String, String> parameters) {
        MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
        
        parameters.entrySet().forEach((keyValue) -> {
            when(multiMap.getFirst(keyValue.getKey())).thenReturn(keyValue.getValue());
        });
        
        when(uriInfo.getQueryParameters()).thenReturn(multiMap);
    }
}
