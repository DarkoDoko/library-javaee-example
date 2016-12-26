package com.library.app.logaudit.interceptor;

import com.library.app.logaudit.model.LogAudit;
import com.library.app.logaudit.repository.LogAuditRepository;
import com.library.app.user.UserNotFoundException;
import com.library.app.user.services.UserServices;
import java.security.Principal;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAuditInterceptor {

    @Inject
    private LogAuditRepository repository;
    
    @Inject
    private UserServices userServices;
    
    @Inject
    private Principal principal;
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @AroundInvoke
    public Object intercept(InvocationContext invocationContext) throws Exception {
        logger.debug("Interceptor being executed...");
        
        Object toReturn = invocationContext.proceed();
        
        try{
            processAuditableAnnotation(invocationContext);
        } catch(UserNotFoundException e) {
            logger.info("No user found for " + principal.getName());
        }
        
        return toReturn;
    }

    private void processAuditableAnnotation(InvocationContext invocationContext) throws UserNotFoundException{
        Auditable auditable = invocationContext.getMethod().getAnnotation(Auditable.class);
        
        if(auditable != null) {
            String elementName = invocationContext.getParameters()[0].getClass().getSimpleName();
            LogAudit logAudit = new LogAudit(userServices.findByEmail(principal.getName()), auditable.action(), elementName);
            logger.debug("Creating log audit {}", logAudit);
            repository.add(logAudit);
        }
    }
}
