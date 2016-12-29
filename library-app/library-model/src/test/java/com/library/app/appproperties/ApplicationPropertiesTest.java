package com.library.app.appproperties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class ApplicationPropertiesTest {

    private ApplicationProperties applicationProperties;
    
    @Before
    public void setupTest(){
        applicationProperties = new ApplicationProperties();
        applicationProperties.init();
    }
    
    @Test
    public void getDaysBeforeOrderExpiration() {
        assertThat(applicationProperties.getDaysBeforeExpiration(), is(equalTo(7)));
    }
    
}
