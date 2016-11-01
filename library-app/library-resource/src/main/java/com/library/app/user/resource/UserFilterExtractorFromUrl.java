package com.library.app.user.resource;

import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.user.model.User.UserType;
import com.library.app.user.model.filter.UserFilter;
import javax.ws.rs.core.UriInfo;

public class UserFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl {

    public UserFilterExtractorFromUrl(UriInfo uriInfo) {
        super(uriInfo);
    }
    
    public UserFilter getFilter() {
        UserFilter filter = new UserFilter();
        filter.setPaginationData(extractPaginationData());
        filter.setName(getUriInfo().getQueryParameters().getFirst("name"));
        
        String userType = getUriInfo().getQueryParameters().getFirst("type");
        if(userType != null) {
            filter.setUserType(UserType.valueOf(userType));
        }
        
        return filter;
    }

    @Override
    protected String getDefaultSortField() {
        return "name";
    }
    
}
