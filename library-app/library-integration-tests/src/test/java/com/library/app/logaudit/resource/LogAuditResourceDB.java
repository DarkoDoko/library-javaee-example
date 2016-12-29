package com.library.app.logaudit.resource;

import com.library.app.logaudit.LogAuditForTestsRepository;
import static com.library.app.logaudit.LogAuditForTestsRepository.allLogs;
import static com.library.app.logaudit.LogAuditForTestsRepository.normalizeDependencies;
import com.library.app.logaudit.repository.LogAuditRepository;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/DB/logsaudit")
public class LogAuditResourceDB {
    
    @Inject
    private LogAuditRepository logAuditRepository;
    
    @PersistenceContext
    EntityManager em;
    
    @POST
    public void addAll(){
        allLogs().forEach((logAudit) -> logAuditRepository.add(normalizeDependencies(logAudit, em)));
    } 
}
