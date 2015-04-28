package org.sumanta.db.util;

public class DBQuery {
  public static String insertCA = "insert into CA(id,dn,serialno,issuer,certificate,keypair) values(?,?,?,?,?,?)";

  public static String insertRootCA = "insert into RootCA(id,dn,serialno,issuer,certificate,keypair) values(?,?,?,?,?,?)";

  public static String insertIssuedCertificate = "insert into IssuedCertificate(id,dn,serialno,issuer,certificate,keypair) values(?,?,?,?,?,?)";
}
