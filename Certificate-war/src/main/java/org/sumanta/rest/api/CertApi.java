package org.sumanta.rest.api;

import java.io.ByteArrayInputStream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.sumanta.cli.CertAdmin;

@Path("/certapi")
public class CertApi {

    @Inject
    CertAdmin certAdmin;
    
    @POST
    @Path("/execute/{command}")
    public String execute(@PathParam("command") String commandString) {
        System.out.println(commandString);
        String response = "success";
        try {
            response = certAdmin.parse(commandString.split(" "));
        } catch (Exception e) {
            response = "Invalid command Syntax";
            response.concat(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/download/{url}")
    @Produces("application/octet-stream")
    public Response download(@PathParam("url") String url) {
        System.out.println(url);
        ResponseBuilder response;
        try {
            byte[] fileinbyte = ContentHolder.getInstance().getHolder().get(url);
            // logic for file
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(fileinbyte);
            response = Response.ok((Object) fileinbyte);
            response.header("Content-Disposition", "attachment; filename=\"cert.crt\"");
            return response.build();
            // return new ByteArrayStreamingOutput(arrayInputStream);
        } catch (Exception e) {
        }
        return null;
    }

}
