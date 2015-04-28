package org.sumanta.db.util;

public class DBSchema {

  public static String IssuedCertificate_table = "create table IssuedCertificate(id int, dn varchar(256),serialno varchar(250),issuer varchar(50),certificate blob(15K),keypair blob(20K))";

  public static String RootCA_table = "create table RootCA(id int, dn varchar(256),serialno varchar(250),issuer varchar(50),certificate blob(15K),keypair blob(20K))";
  public static String CA_table = "create table CA(id int, dn varchar(256),serialno varchar(250),issuer varchar(50),certificate blob(15K),keypair blob(20K))";

  public static String CRL_table = "create table CRL(serialno varchar(250),revokeddate date)";
}