package com.library.app.commontests.utils;

import javax.persistence.EntityManager;

public final class TestRepositoryUtils {
    
    public static <T> T findByPropertyNameAndValue(EntityManager em, Class<T> clazz, String propertyName, String propertyValue) {
        return (T) em.createQuery("SELECT o FROM " + clazz.getSimpleName() + " o "
                                + "WHERE o." + propertyName + " =:propertyValue")
                    .setParameter("propertyValue", propertyValue)
                    .getSingleResult();
    }
}
