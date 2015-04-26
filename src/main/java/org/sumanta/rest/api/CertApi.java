package org.sumanta.rest.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;


@Path("/certapi")
public class CertApi {

	@POST
	@Path("/execute/{command}")
	public String execute(@PathParam("command") String commandString){
		return "success";
	}
	
}
