package org.sumanta.cli;

import javax.ejb.Local;

@Local
public interface CertAdmin {

    public String parse(final String[] args) throws Exception;

}
