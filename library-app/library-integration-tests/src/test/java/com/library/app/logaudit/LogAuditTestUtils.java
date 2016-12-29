package com.library.app.logaudit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.library.app.common.model.HttpCode;
import com.library.app.commontests.utils.IntTestUtils;
import com.library.app.commontests.utils.ResourceClient;
import com.library.app.logaudit.model.LogAudit;
import static com.library.app.user.UserForTestsRepository.admin;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LogAuditTestUtils {
    
	public static void assertAuditLogs(final ResourceClient resourceClient, final int expectedTotalRecords,
			final LogAudit... expectedLogsAudit) {

		final String resourceName = "logsaudit";
		final Response response = resourceClient.user(admin())
				.resourcePath(resourceName + "?page=0&per_page=10&sort=+createdAt").get();
		assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));

		final JsonArray logsList = IntTestUtils.assertJsonHasTheNumberOfElementsAndReturnTheEntries(response,
				expectedTotalRecords, expectedLogsAudit.length);

		for (int i = 0; i < expectedLogsAudit.length; i++) {
			final LogAudit expectedLogAudit = expectedLogsAudit[i];
			final JsonObject actualLogAudit = logsList.get(i).getAsJsonObject();
			assertThat(actualLogAudit.getAsJsonObject("user").get("name").getAsString(), is(equalTo(expectedLogAudit
					.getUser().getName())));
			assertThat(actualLogAudit.get("action").getAsString(), is(equalTo(expectedLogAudit.getAction().toString())));
			assertThat(actualLogAudit.get("element").getAsString(), is(equalTo(expectedLogAudit.getElement())));
		}
	}
}
