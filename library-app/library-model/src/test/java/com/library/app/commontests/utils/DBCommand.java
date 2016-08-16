package com.library.app.commontests.utils;

import org.junit.Ignore;

/**
 *
 * @author ddoko
 * @param <T>
 */
@Ignore
public interface DBCommand<T> {
    
    T execute();
    
}
