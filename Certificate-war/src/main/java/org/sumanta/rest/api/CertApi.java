package org.sumanta.rest.api;

import java.io.ByteArrayInputStream;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.sumanta.cli.CertAdmin;

@Path("/certapi")
public class CertApi {

    @EJB
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
