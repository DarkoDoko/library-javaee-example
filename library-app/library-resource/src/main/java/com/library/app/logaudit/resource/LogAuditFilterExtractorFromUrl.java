package com.library.app.logaudit.resource;

import com.library.app.DateUtils;
import com.library.app.common.resource.AbstractFilterExtractorFromUrl;
import com.library.app.logaudit.model.LogAuditFilter;
import javax.ws.rs.core.UriInfo;

public class LogAuditFilterExtractorFromUrl extends AbstractFilterExtractorFromUrl{

    public LogAuditFilterExtractorFromUrl(UriInfo uriInfo) {
        super(uriInfo);
    }
    
    public LogAuditFilter getFilter() {
		LogAuditFilter logAuditFilter = new LogAuditFilter();

		logAuditFilter.setPaginationData(extractPaginationData());

		String startDateStr = getUriInfo().getQueryParameters().getFirst("startDate");
		if (startDateStr != null) {
			logAuditFilter.setStartDate(DateUtils.getAsDateTime(startDateStr));
		}

		String endDateStr = getUriInfo().getQueryParameters().getFirst("endDate");
		if (endDateStr != null) {
			logAuditFilter.setEndDate(DateUtils.getAsDateTime(endDateStr));
		}

		final String userIdStr = getUriInfo().getQueryParameters().getFirst("userId");
		if (userIdStr != null) {
			logAuditFilter.setUserId(Long.valueOf(userIdStr));
		}

		return logAuditFilter;
	}

    @Override
    protected String getDefaultSortField() {
        return "-createdAt";
    }
    
}
