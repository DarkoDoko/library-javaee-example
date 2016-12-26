package com.library.app.logaudit.interceptor;

import com.library.app.logaudit.model.LogAudit.Action;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    
    Action action();
    
}
