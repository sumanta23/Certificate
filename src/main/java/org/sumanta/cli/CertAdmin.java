package org.sumanta.cli;

import org.sumanta.cert.SamCA;

public class CertAdmin extends CertAdminInterface {

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
        SamCA.createCA(ca.issuer, ca.commonName, ca.validity, null);
      } else if (ca.opt.equals(Operation.list)) {
        try {
          result = SamCA.listCertificate(ca.type, ca.commonName, ca.serialno);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (ca.opt.equals(Operation.export)) {
        try {
          SamCA.exportCertificateToFile(ca.type, ca.serialno, ca.tofile, ca.cat, ca.format);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else if (ca.type.equals(Type.rootca)) {
      if (ca.opt.equals(Operation.create)) {
        SamCA.createRootCA(ca.commonName, ca.validity);
      } else if (ca.opt.equals(Operation.list)) {
        try {
          result = SamCA.listCertificate(ca.type, ca.commonName, ca.serialno);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (ca.opt.equals(Operation.export)) {
        try {
          SamCA.exportCertificateToFile(ca.type, ca.serialno, ca.tofile, ca.cat, ca.format);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else if (ca.type.equals(Type.certificate)) {
      if (ca.opt.equals(Operation.create)) {
        SamCA.createCertificate(ca.issuer, ca.commonName, ca.validity, null);
      } else if (ca.opt.equals(Operation.list)) {
        try {
          SamCA.listCertificate(ca.type, ca.commonName, ca.serialno);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (ca.opt.equals(Operation.export)) {
        try {
          SamCA.exportCertificateToFile(ca.type, ca.serialno, ca.tofile, ca.cat, ca.format);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return result;
  }

}
