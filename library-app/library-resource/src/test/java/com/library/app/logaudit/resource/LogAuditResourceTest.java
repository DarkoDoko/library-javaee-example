package com.library.app.logaudit.resource;

import com.library.app.common.model.HttpCode;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileResponse;
import static com.library.app.commontests.utils.JsonTestUtils.assertJsonMatchesFileContent;
import static com.library.app.logaudit.LogAuditForTestsRepository.allLogs;
import static com.library.app.logaudit.LogAuditForTestsRepository.logAuditWithId;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.LogAuditFilter;
import com.library.app.logaudit.repository.LogAuditRepository;
import com.library.app.pagination.PaginatedData;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class LogAuditResourceTest {
	private LogAuditResource logAuditResource;

	@Mock
	private LogAuditRepository logAuditRepository;

	@Mock
	private UriInfo uriInfo;

	private static final String PATH_RESOURCE = "logsaudit";
    
    @Before
	public void initTestCase() {
		MockitoAnnotations.initMocks(this);

		logAuditResource = new LogAuditResource();

		logAuditResource.logAuditRepository = logAuditRepository;
		logAuditResource.uriInfo = uriInfo;
		logAuditResource.logAuditJsonConverter = new LogAuditJsonConverter();
	}

    @Test
	public void findByFilter() {
		final MultivaluedMap<String, String> multiMap = mock(MultivaluedMap.class);
		when(uriInfo.getQueryParameters()).thenReturn(multiMap);

		when(logAuditRepository.findByFilter((LogAuditFilter) anyObject())).thenReturn(
				new PaginatedData<LogAudit>(3, getLogs()));

		final Response response = logAuditResource.findByFilter();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
		assertJsonResponseWithFile(response, "allLogs.json");
	}
    
	private List<LogAudit> getLogs() {
		final List<LogAudit> logs = allLogs();

		logAuditWithId(logs.get(0), 1L);
		logs.get(0).getUser().setId(1L);

		logAuditWithId(logs.get(1), 2L);
		logs.get(1).getUser().setId(1L);

		logAuditWithId(logs.get(2), 3L);
		logs.get(2).getUser().setId(2L);

		return logs;
	}   
    
    private void assertJsonResponseWithFile(final Response response, final String fileName) {
		assertJsonMatchesFileContent(response.getEntity().toString(), getPathFileResponse(PATH_RESOURCE, fileName));
	}
}
