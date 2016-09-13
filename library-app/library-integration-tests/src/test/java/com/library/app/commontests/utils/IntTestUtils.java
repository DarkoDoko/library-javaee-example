package com.library.app.commontests.utils;

import com.library.app.common.model.HttpCode;
import static com.library.app.commontests.utils.FileTestNameUtils.getPathFileRequest;
import javax.ws.rs.core.Response;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;

@Ignore
public class IntTestUtils {
    
    public static Long addElementWithFileAndGetId(ResourceClient client, String path, String mainFolder, String fileName){
        Response response = client.resourcePath(path).postWithFile(getPathFileRequest(mainFolder, fileName));
        
        return assertResponseIsCreatedAndGetId(response);
    }
    
    public static String findById(ResourceClient client, String path, Long id){
        Response response = client.resourcePath(path + "/" + id).get();
        
        assertThat(response.getStatus(), is(equalTo(HttpCode.OK.getCode())));
        
        return response.readEntity(String.class);
    }
    
    private static Long assertResponseIsCreatedAndGetId(Response response){
        assertThat(response.getStatus(), is(equalTo(HttpCode.CREATED.getCode())));
        
        Long id = JsonTestUtils.getIdFromJson(response.readEntity(String.class));
        
        assertThat(id, is(notNullValue()));
        return id;
    }
}
