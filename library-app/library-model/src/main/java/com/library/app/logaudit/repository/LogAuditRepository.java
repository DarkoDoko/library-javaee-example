package com.library.app.logaudit.repository;

import com.library.app.GenericRepository;
import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.model.LogAuditFilter;
import com.library.app.pagination.PaginatedData;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class LogAuditRepository extends GenericRepository<LogAudit>{

    @PersistenceContext
    EntityManager em;
    
    @Override
    protected Class<LogAudit> getPersistentClass() {
        return LogAudit.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public PaginatedData<LogAudit> findByFilter(LogAuditFilter logAuditFilter) {
		StringBuilder clause = new StringBuilder("WHERE e.id IS NOT NULL");
		Map<String, Object> queryParameters = new HashMap<>();
		if (logAuditFilter.getStartDate() != null) {
			clause.append(" AND e.createdAt >= :startDate");
			queryParameters.put("startDate", logAuditFilter.getStartDate());
		}
		if (logAuditFilter.getEndDate() != null) {
			clause.append(" AND e.createdAt <= :endDate");
			queryParameters.put("endDate", logAuditFilter.getEndDate());
		}
		if (logAuditFilter.getUserId() != null) {
			clause.append(" AND e.user.id = :userId");
			queryParameters.put("userId", logAuditFilter.getUserId());
		}

		return findByParameters(clause.toString(), logAuditFilter.getPaginationData(), queryParameters,
				"createdAt DESC");
	}
    
}
