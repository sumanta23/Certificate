package org.sumanta.rest.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.sumanta.bean.RootCA;
import org.sumanta.cli.CertAdmin;

import com.google.gson.Gson;


@Path("/certapi")
public class CertApi {

	@POST
	@Path("/execute/{command}")
	public String execute(@PathParam("command") String commandString){
		System.out.println(commandString);
		String response="success";
		CertAdmin certAdmin=new CertAdmin();
		try{
			response=certAdmin.parse(commandString.split(" "));
		}
		catch(Exception e){
			response="Invalid command Syntax";
		}
		return response;
	}
	
}
