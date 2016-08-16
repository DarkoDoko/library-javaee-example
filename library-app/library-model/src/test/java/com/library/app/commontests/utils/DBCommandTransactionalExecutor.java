package com.library.app.commontests.utils;

import javax.persistence.EntityManager;
import org.junit.Ignore;

/**
 *
 * @author ddoko
 */
@Ignore
public class DBCommandTransactionalExecutor {
    
    private EntityManager em;

    public DBCommandTransactionalExecutor(EntityManager em) {
        this.em = em;
    }
    
    public <T> T executeCommand(DBCommand<T> dbCommand){
        try{
            em.getTransaction().begin();
            
            T toReturn = dbCommand.execute();
            
            em.getTransaction().commit();
            em.clear();
            return toReturn;
        } catch (Exception e){
             System.out.println(e.getMessage());
             em.getTransaction().rollback();
             throw new IllegalStateException(e);
        }
    }
    
}
