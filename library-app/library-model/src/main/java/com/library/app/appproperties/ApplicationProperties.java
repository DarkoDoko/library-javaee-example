package com.library.app.appproperties;

import java.io.IOException;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationProperties {
    
    private Properties properties;
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @PostConstruct
    public void init(){
        try{
            properties = new Properties();
            properties.load(ApplicationProperties.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch(IOException e){
            logger.error("Error wile reading app properties file");
            throw new IllegalArgumentException(e);
        }
    }
    
    public int getDaysBeforeExpiration(){
        return Integer.valueOf(properties.getProperty("days-before-order-expiration"));
    }
    
}
