package org.sumanta.rest.config;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.sumanta.rest.api.CertApi;

@ApplicationPath("/restapi")
public class ConfigApplication extends Application {

    private Set<Object> singletons = new HashSet<Object>();

    public ConfigApplication() {
        singletons.add(new CertApi());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}