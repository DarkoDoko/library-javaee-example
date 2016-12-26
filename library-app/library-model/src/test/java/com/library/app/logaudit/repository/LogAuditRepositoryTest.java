package com.library.app.logaudit.repository;

import com.library.app.DateUtils;
import com.library.app.commontests.utils.TestBaseRepository;
import static com.library.app.commontests.utils.TestRepositoryUtils.findByPropertyNameAndValue;
import static com.library.app.logaudit.LogAuditForTestsRepository.allLogs;
import static com.library.app.logaudit.LogAuditForTestsRepository.normalizeDependencies;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.LogAuditFilter;
import com.library.app.pagination.PaginatedData;
import com.library.app.pagination.filter.PaginationData;
import com.library.app.pagination.filter.PaginationData.OrderMode;
import static com.library.app.user.UserForTestsRepository.admin;
import static com.library.app.user.UserForTestsRepository.allUsers;
import com.library.app.user.model.Employee;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;

public class LogAuditRepositoryTest extends TestBaseRepository{
    
    private LogAuditRepository repositoryUnderTest;
    
    @Before
    public void initTestCase() {
        initializeTestDB();
        
        repositoryUnderTest = new LogAuditRepository();
        repositoryUnderTest.em = em;
        
        loadUsers();
        loadForFindByFilter();
    }
    
    @After
	public void setDownTestCase() {
		closeEntityManager();
	}

	@Test
	public void findByFilterNoFilter() {
		final PaginatedData<LogAudit> logs = repositoryUnderTest.findByFilter(new LogAuditFilter());
		assertThat(logs.getNumberOfRows(), is(equalTo(3)));
		assertThat(logs.getRows().size(), is(equalTo(3)));
		assertThat(DateUtils.formatDateTime(logs.getRow(0).getCreatedAt()), is(equalTo("2015-01-10T19:32:22Z")));
		assertThat(DateUtils.formatDateTime(logs.getRow(1).getCreatedAt()), is(equalTo("2015-01-09T19:32:22Z")));
		assertThat(DateUtils.formatDateTime(logs.getRow(2).getCreatedAt()), is(equalTo("2015-01-08T19:32:22Z")));
	}

	@Test
	public void findByFilterWithPagination() {
		final LogAuditFilter logAuditFilter = new LogAuditFilter();
		logAuditFilter.setPaginationData(new PaginationData(0, 2, "createdAt", OrderMode.ASCENDING));

		PaginatedData<LogAudit> logs = repositoryUnderTest.findByFilter(logAuditFilter);
		assertThat(logs.getNumberOfRows(), is(equalTo(3)));
		assertThat(logs.getRows().size(), is(equalTo(2)));
		assertThat(DateUtils.formatDateTime(logs.getRow(0).getCreatedAt()), is(equalTo("2015-01-08T19:32:22Z")));
		assertThat(DateUtils.formatDateTime(logs.getRow(1).getCreatedAt()), is(equalTo("2015-01-09T19:32:22Z")));

		logAuditFilter.setPaginationData(new PaginationData(2, 2, "createdAt", OrderMode.ASCENDING));

		logs = repositoryUnderTest.findByFilter(logAuditFilter);
		assertThat(logs.getNumberOfRows(), is(equalTo(3)));
		assertThat(logs.getRows().size(), is(equalTo(1)));
		assertThat(DateUtils.formatDateTime(logs.getRow(0).getCreatedAt()), is(equalTo("2015-01-10T19:32:22Z")));
	}

	@Test
	public void findByFilterFinteringByDate() {
		final LogAuditFilter logAuditFilter = new LogAuditFilter();
		logAuditFilter.setStartDate(DateUtils.getAsDateTime("2015-01-09T19:32:22Z"));
		logAuditFilter.setEndDate(DateUtils.getAsDateTime("2015-01-10T19:32:22Z"));
		logAuditFilter.setPaginationData(new PaginationData(0, 2, "createdAt", OrderMode.DESCENDING));

		final PaginatedData<LogAudit> logs = repositoryUnderTest.findByFilter(logAuditFilter);
		assertThat(logs.getNumberOfRows(), is(equalTo(2)));
		assertThat(logs.getRows().size(), is(equalTo(2)));
		assertThat(DateUtils.formatDateTime(logs.getRow(0).getCreatedAt()), is(equalTo("2015-01-10T19:32:22Z")));
		assertThat(DateUtils.formatDateTime(logs.getRow(1).getCreatedAt()), is(equalTo("2015-01-09T19:32:22Z")));
	}

	@Test
	public void findByFilterFinteringByUser() {
		final LogAuditFilter logAuditFilter = new LogAuditFilter();
		logAuditFilter.setUserId(findByPropertyNameAndValue(em, Employee.class, "email", admin().getEmail()).getId());
		logAuditFilter.setPaginationData(new PaginationData(0, 2, "createdAt", OrderMode.DESCENDING));

		final PaginatedData<LogAudit> logs = repositoryUnderTest.findByFilter(logAuditFilter);
		assertThat(logs.getNumberOfRows(), is(equalTo(2)));
		assertThat(logs.getRows().size(), is(equalTo(2)));
		assertThat(DateUtils.formatDateTime(logs.getRow(0).getCreatedAt()), is(equalTo("2015-01-09T19:32:22Z")));
		assertThat(DateUtils.formatDateTime(logs.getRow(1).getCreatedAt()), is(equalTo("2015-01-08T19:32:22Z")));

	}
    
    private void loadUsers() {
		dbExecutor.executeCommand(() -> {
			allUsers().forEach(em::persist);
			return null;
		});
	}
    
    private void loadForFindByFilter() {
		dbExecutor.executeCommand(() -> {
			allLogs().forEach((logAudit) -> repositoryUnderTest.add(normalizeDependencies(logAudit, em)));
			return null;
		});
	}
    
}
