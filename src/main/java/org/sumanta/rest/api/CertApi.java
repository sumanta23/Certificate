package org.sumanta.rest.api;

import java.io.ByteArrayInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.sumanta.cli.CertAdmin;
import org.sumanta.rest.config.ByteArrayStreamingOutput;

@Path("/certapi")
public class CertApi {

  @POST
  @Path("/execute/{command}")
  public String execute(@PathParam("command") String commandString) {
    System.out.println(commandString);
    String response = "success";
    CertAdmin certAdmin = new CertAdmin();
    try {
      response = certAdmin.parse(commandString.split(" "));
    } catch (Exception e) {
      response = "Invalid command Syntax";
    }
    return response;
  }
  
  @GET
  @Path("/download/{url}")
  @Produces("application/octet-stream")
  public Response download(@PathParam("url") String url) {
    System.out.println(url);
    ResponseBuilder response;
    CertAdmin certAdmin = new CertAdmin();
    try {
      byte[] fileinbyte=ContentHolder.getInstance().getHolder().get(url);
      //logic for file
      ByteArrayInputStream arrayInputStream=new ByteArrayInputStream(fileinbyte);
      response = Response.ok((Object) fileinbyte);
      response.header("Content-Disposition", "attachment; filename=\"cert.crt\"");
      return response.build();
      //return new ByteArrayStreamingOutput(arrayInputStream);
    } catch (Exception e) {
    }
    return null;
  }

}
