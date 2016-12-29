package com.library.app.appproperties;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

public class ApplicationPropertiesLoaderExtension implements Extension{
    
    private Properties properties;
    
    public <T> void initializePropertyValues(@Observes ProcessInjectionTarget<T> pit) {
        AnnotatedType<T> at = pit.getAnnotatedType();
        
        InjectionTarget<T> it = pit.getInjectionTarget();
        InjectionTarget<T> wrapper = new InjectionTarget<T>() {
            @Override
            public void inject(T instance, CreationalContext<T> ctx) {
                it.inject(instance, ctx);
                for(Field field : at.getJavaClass().getDeclaredFields()){
                    PropertyValue annotation = field.getAnnotation(PropertyValue.class);
                    if(annotation != null){
                        String propertyName = annotation.name();
                        field.setAccessible(true);
                        Class<?> fieldType = field.getType();
                        try{
                            if(fieldType == Integer.class){
                                String value = getPropertyValue(propertyName);
                                field.set(instance, Integer.valueOf(value));
                            } 
                        } catch(IllegalArgumentException | IllegalAccessException e) {
                            
                        }
                    }
                }
            }

            @Override
            public void postConstruct(T instance) {
                it.postConstruct(instance);
            }

            @Override
            public void preDestroy(T instance) {
                it.preDestroy(instance);
            }

            @Override
            public T produce(CreationalContext<T> ctx) {
                return it.produce(ctx);
            }

            @Override
            public void dispose(T instance) {
                it.dispose(instance);
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }
        };
        pit.setInjectionTarget(wrapper);
    }
    
    private String getPropertyValue(final String propertyName) {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
		return properties.getProperty(propertyName);
	}
    
}
