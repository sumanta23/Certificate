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
            Content content = ContentHolder.getInstance().getHolder().get(url);
            // logic for file
            String contentName = content.getContentName();
            contentName = contentName != null ? contentName.replace("."+content.getContentType(), "") : "default";
            
            ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(content.getByteContent());
            response = Response.ok((Object) content.getByteContent());
            System.out.println(content.getContentType());
            response.header("Content-Disposition", "attachment; filename=\"" + contentName + "." + content.getContentType()+"\"");
            return response.build();
            // return new ByteArrayStreamingOutput(arrayInputStream);
        } catch (Exception e) {
        }
        return null;
    }

}
