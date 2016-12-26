package com.library.app.logaudit;

import com.library.app.DateUtils;
import static com.library.app.commontests.utils.TestRepositoryUtils.findByPropertyNameAndValue;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.LogAudit.Action;
import static com.library.app.user.UserForTestsRepository.admin;
import static com.library.app.user.UserForTestsRepository.johnDoe;
import com.library.app.user.model.User;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.Ignore;

@Ignore
public class LogAuditForTestsRepository {
    
    public static List<LogAudit> allLogs(){
        final LogAudit logAudit1 = new LogAudit(admin(), Action.ADD, "Category");
		logAudit1.setCreatedAt(DateUtils.getAsDateTime("2015-01-08T19:32:22Z"));

		final LogAudit logAudit2 = new LogAudit(admin(), Action.UPDATE, "Category");
		logAudit2.setCreatedAt(DateUtils.getAsDateTime("2015-01-09T19:32:22Z"));

		final LogAudit logAudit3 = new LogAudit(johnDoe(), Action.ADD, "Order");
		logAudit3.setCreatedAt(DateUtils.getAsDateTime("2015-01-10T19:32:22Z"));

		return Arrays.asList(logAudit1, logAudit2, logAudit3);
    }
    
    public static LogAudit logAuditWithId(final LogAudit logAudit, final Long id) {
		logAudit.setId(id);
		return logAudit;
	}

    public static LogAudit normalizeDependencies(final LogAudit logAudit, final EntityManager em) {
		logAudit.setUser(findByPropertyNameAndValue(em, User.class, "email", logAudit.getUser().getEmail()));
		return logAudit;
	}
}
