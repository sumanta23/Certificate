package org.sumanta.cert;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.*;

import org.bouncycastle.openssl.PEMReader;
import org.sumanta.db.util.Constant;
import org.sumanta.db.util.DBQuery;

public class CreateRootCA {

  private static PrintStream out = System.out;

  public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException,
      CertificateEncodingException, InvalidKeyException, SignatureException, IOException, SQLException {

    final KeyPair pair = SamCA.generateKeyPairAndSaveToFile("rootkeypair");
    SamCA.generateCACertificateAndSaveToFile("RootCA.crt", pair);

    X509Certificate cert = SamCA.loadCertificateFromFile("RootCA.crt");

    out.println(System.currentTimeMillis());
    final String str = "CertDB";
    final Connection localConnection = DriverManager.getConnection(Constant.protocol + str);
    final PreparedStatement ps = localConnection.prepareStatement(DBQuery.insertCA);
    ps.setInt(1, 1);
    ps.setString(2, cert.getSubjectDN().toString());
    ps.setString(3, cert.getSerialNumber().toString());
    ps.setString(4, cert.getIssuerDN().toString());
    InputStream is = new FileInputStream("RootCA.crt");
    ps.setBlob(5, is);
    is = new FileInputStream("rootkeypair");
    ps.setBlob(6, is);

    ps.executeUpdate();
    localConnection.commit();
    out.println(System.currentTimeMillis());
    Statement st = localConnection.createStatement();
    ResultSet rs = st.executeQuery("select * from CA");
    while (rs.next()) {
      out.println(rs.getInt(1));
      out.println(rs.getString(2));
      Blob ce = rs.getBlob(5);
      InputStream reader = ce.getBinaryStream();

      try {
        final Reader r = new InputStreamReader(reader);
        Object object;
        final PEMReader pemReader = new PEMReader(r);
        while ((object = pemReader.readObject()) != null) {
          if (object instanceof X509Certificate) {
            cert = (X509Certificate) object;
          }
        }

        out.println(cert.getNotBefore());
      } catch (Exception e) {

      }
    }
    out.println(System.currentTimeMillis());
  }

}
