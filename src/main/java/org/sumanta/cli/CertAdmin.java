package org.sumanta.cli;

import org.sumanta.cert.SamCA;

public class CertAdmin extends CertAdminInterface {

  
  SamCA samCA=new SamCA();
  
  /**
   * @param args
   * @throws Exception
   */
  public String parse(final String[] args) throws Exception {
    CertAdmin ca = new CertAdmin();
    ca.parseCli(args);
    String result = "";
    if (ca.type.equals(Type.ca)) {
      if (ca.opt.equals(Operation.create)) {
        samCA.createCA(ca.issuer, ca.commonName, ca.validity, null);
      } else if (ca.opt.equals(Operation.list)) {
        try {
          result = samCA.listCertificate(ca.type, ca.commonName, ca.serialno);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (ca.opt.equals(Operation.export)) {
        try {
          samCA.exportCertificateToFile(ca.type, ca.serialno, ca.tofile, ca.cat, ca.format);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else if (ca.type.equals(Type.rootca)) {
      if (ca.opt.equals(Operation.create)) {
        samCA.createRootCA(ca.commonName, ca.validity);
      } else if (ca.opt.equals(Operation.list)) {
        try {
          result = samCA.listCertificate(ca.type, ca.commonName, ca.serialno);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (ca.opt.equals(Operation.export)) {
        try {
          String fileid = samCA.exportCertificateToFile(ca.type, ca.serialno, ca.tofile, ca.cat, ca.format);
          return fileid;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else if (ca.type.equals(Type.certificate)) {
      if (ca.opt.equals(Operation.create)) {
        samCA.createCertificate(ca.issuer, ca.commonName, ca.validity, null);
      } else if (ca.opt.equals(Operation.list)) {
        try {
          samCA.listCertificate(ca.type, ca.commonName, ca.serialno);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (ca.opt.equals(Operation.export)) {
        try {
          samCA.exportCertificateToFile(ca.type, ca.serialno, ca.tofile, ca.cat, ca.format);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }

}
