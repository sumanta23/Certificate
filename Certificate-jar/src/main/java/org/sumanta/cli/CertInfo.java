/**
 * 
 */
package org.sumanta.cli;

/**
 * @author sam
 *
 */
public class CertInfo {

    public Type type = Type.certificate;
    public Operation opt = Operation.list;
    public String issuer = "";
    public long validity = 100;
    public String commonName = "";
    public String serialno = "";
    public String tofile = "";
    public Category cat = Category.certificate;
    public Format format = Format.crt;

}
