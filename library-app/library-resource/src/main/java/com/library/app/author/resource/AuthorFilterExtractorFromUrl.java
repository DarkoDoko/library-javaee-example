package com.library.app.author.resource;

import com.library.app.author.model.AuthorFilter;
import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import javax.ws.rs.core.UriInfo;

public class AuthorFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {
    
    public AuthorFilterExtractorFromUrl(UriInfo uriInfo) {
        super(uriInfo);
    }

    AuthorFilter getFilter() {
        AuthorFilter authorFilter = new AuthorFilter();
        authorFilter.setPaginationData(extractPaginationData());
        authorFilter.setName(getUriInfo().getQueryParameters().getFirst("name"));
        return authorFilter;
    }

    @Override
    protected String getDefaultSortField() {
        return "name";
    }
}
