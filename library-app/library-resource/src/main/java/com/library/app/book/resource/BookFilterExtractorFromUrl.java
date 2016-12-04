package com.library.app.book.resource;

import com.library.app.book.model.BookFilter;
import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import javax.ws.rs.core.UriInfo;

public class BookFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl{

    public BookFilterExtractorFromUrl(UriInfo uriInfo) {
        super(uriInfo);
    }
    
    public BookFilter getFilter() {
        BookFilter bookFilter = new BookFilter();
        
        bookFilter.setPaginationData(extractPaginationData());
        bookFilter.setTitle(getUriInfo().getQueryParameters().getFirst("title"));
        
        String categoryIdStr = getUriInfo().getQueryParameters().getFirst("categoryId");
        if(categoryIdStr != null){
            bookFilter.setCategoryId(Long.valueOf(categoryIdStr));
        }
        
        return bookFilter;
    }

    @Override
    protected String getDefaultSortField() {
        return "title";
    }
    
}
