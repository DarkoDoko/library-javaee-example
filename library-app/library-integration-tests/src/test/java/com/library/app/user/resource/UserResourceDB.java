package com.library.app.user.resource;

import static com.library.app.user.UserForTestsRepository.admin;
import static com.library.app.user.UserForTestsRepository.allUsers;
import com.library.app.user.services.UserServices;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/DB/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResourceDB {
    
    @Inject
    private UserServices services;
    
    @POST
    public void addAll() {
        allUsers().forEach(services::add);
    }
    
    @POST
    @Path("/admin")
    public void addAdmin() {
        services.add(admin());
    }
    
}
