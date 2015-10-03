package org.sumanta.cert;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.sumanta.cli.*;
import org.sumanta.cert.SamCA;
import org.sumanta.util.Constants;

@Stateless
public class CertAdminImpl implements CertAdmin {

    @Inject
    private SamCA samCA;
    
    
    CertInfo certInfo=new CertInfo();
    
    
    /**
     * @param args
     * @throws Exception
     */
    public String parse(final String[] args) throws Exception {
        CertAdminImpl ca = new CertAdminImpl();
        if (args[0].equalsIgnoreCase("help")) {
            return help();
        }

        ca.parseCli(args);
        String result = "";

        if (ca.certInfo.type.equals(Type.ca)) {
            if (ca.certInfo.opt.equals(Operation.create)) {
                samCA.createCA(ca.certInfo.issuer, ca.certInfo.commonName, ca.certInfo.validity, null);
            } else if (ca.certInfo.opt.equals(Operation.list)) {
                try {
                    result = samCA.listCertificate(ca.certInfo.type, ca.certInfo.commonName, ca.certInfo.serialno);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ca.certInfo.opt.equals(Operation.export)) {
                try {
                    String fileid = samCA.exportCertificateToFile(ca.certInfo.type, ca.certInfo.serialno, ca.certInfo.tofile, ca.certInfo.cat, ca.certInfo.format);
                    return fileid;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (ca.certInfo.type.equals(Type.rootca)) {
            if (ca.certInfo.opt.equals(Operation.create)) {
                samCA.createRootCA(ca.certInfo.commonName, ca.certInfo.validity);
            } else if (ca.certInfo.opt.equals(Operation.list)) {
                try {
                    result = samCA.listCertificate(ca.certInfo.type, ca.certInfo.commonName, ca.certInfo.serialno);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ca.certInfo.opt.equals(Operation.export)) {
                try {
                    String fileid = samCA.exportCertificateToFile(ca.certInfo.type, ca.certInfo.serialno, ca.certInfo.tofile, ca.certInfo.cat, ca.certInfo.format);
                    return fileid;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (ca.certInfo.type.equals(Type.certificate)) {
            if (ca.certInfo.opt.equals(Operation.create)) {
                samCA.createCertificate(ca.certInfo.issuer, ca.certInfo.commonName, ca.certInfo.validity, null);
            } else if (ca.certInfo.opt.equals(Operation.list)) {
                try {
                    result = samCA.listCertificate(ca.certInfo.type, ca.certInfo.commonName, ca.certInfo.serialno);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (ca.certInfo.opt.equals(Operation.export)) {
                try {
                    String fileid = samCA.exportCertificateToFile(ca.certInfo.type, ca.certInfo.serialno, ca.certInfo.tofile, ca.certInfo.cat, ca.certInfo.format);
                    return fileid;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    
    
    public static String help() {
        String out = "";

        out = "create rootca -validity <days> -cn <>" + "\n";
        out = out + "create ca|certificate -issuer <> -validity <days> -cn <>" + "\n";

        out = out + "list rootca|ca|certificate [-cn <>] [-serial <>]" + "\n";
        out = out + "export -cat <keystore|truststore|certificate> rootca|ca|certificate -serialno <> -filename <> -format pem|crt|jks|der|p12" + "\n";

        return out;
    }

    public void parseCli(final String[] argument) {
        String[] arg = argument;
        final int noOfArg = argument.length;
        int index = 0;
        for (int it = 0; it < noOfArg; it++) {
            if (argument[it].startsWith("\"")) {
                arg[index] = arg[index] + argument[it];
                if (arg[index].endsWith("\"")) {
                    arg[index].replace("\"", "");
                    index++;
                } else {
                    continue;
                }
            } else {
                arg[index] = argument[it].trim();
                index++;
            }
        }

        // create
        if (arg[0].equalsIgnoreCase(Constants.CREATE)) {
            certInfo.opt = Operation.create;
            if (arg[1].equalsIgnoreCase(Constants.CA)) {
                certInfo.type = Type.ca;
                if (arg[2].equalsIgnoreCase(Constants.ISSUER)) {
                    certInfo.issuer = arg[3];
                    if (arg[4].equalsIgnoreCase(Constants.VALIDITY)) {
                        certInfo.validity = Integer.parseInt(arg[5]);
                        if (arg[6].equalsIgnoreCase(Constants.CN)) {
                            certInfo.commonName = arg[7];
                        } else {
                            help();
                        }
                    } else {
                        help();
                    }
                } else {
                    help();
                }
            }

            else {
                if (arg[1].equalsIgnoreCase(Constants.ROOTCA)) {
                    certInfo.type = Type.rootca;
                    if (arg[2].equalsIgnoreCase(Constants.VALIDITY)) {
                        certInfo.validity = Integer.parseInt(arg[3]);
                        if (arg[4].equalsIgnoreCase(Constants.CN)) {
                            certInfo.commonName = arg[5];
                        } else {
                            help();
                        }
                    } else {
                        help();
                    }
                }

                else {
                    if (arg[1].equalsIgnoreCase(Constants.CERTIFICATE)) {
                        certInfo.type = Type.certificate;
                        if (arg[2].equalsIgnoreCase(Constants.ISSUER)) {
                            certInfo.issuer = arg[3];
                            if (arg[4].equalsIgnoreCase(Constants.VALIDITY)) {
                                certInfo.validity = Integer.parseInt(arg[5]);
                                if (arg[6].equalsIgnoreCase(Constants.CN)) {
                                    certInfo.commonName = arg[7];
                                } else {
                                    help();
                                }
                            } else {
                                help();
                            }
                        } else {
                            help();
                        }
                    }

                }
            }

        }

        // list
        if (arg[0].equalsIgnoreCase(Constants.LIST)) {
            certInfo.opt = Operation.list;
            if (arg[1].equalsIgnoreCase(Constants.CA)) {
                certInfo.type = Type.ca;
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.CN)) {
                    certInfo.commonName = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                        certInfo.serialno = arg[5];
                    }
                }
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.SERIALNO)) {
                    certInfo.serialno = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.CN)) {
                        certInfo.commonName = arg[5];
                    }
                }
            } else if (arg[1].equalsIgnoreCase(Constants.ROOTCA)) {
                certInfo.type = Type.rootca;
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.CN)) {
                    certInfo.commonName = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                        certInfo.serialno = arg[5];
                    }
                }
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.SERIALNO)) {
                    certInfo.serialno = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.CN)) {
                        certInfo.commonName = arg[5];
                    }
                }
            } else if (arg[1].equalsIgnoreCase(Constants.CERTIFICATE)) {
                certInfo.type = Type.certificate;
                if (arg[1].equalsIgnoreCase(Constants.CA)) {
                    certInfo.type = Type.ca;
                    if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.CN)) {
                        certInfo.commonName = arg[3];
                        if (arg.length > 3 && arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                            certInfo.serialno = arg[5];
                        }
                    }
                    if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.SERIALNO)) {
                        certInfo.serialno = arg[3];
                        if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.CN)) {
                            certInfo.commonName = arg[5];
                        }
                    }
                }
            }
        }

        // export
        if (arg[0].equalsIgnoreCase(Constants.EXPORT)) {
            certInfo.opt = Operation.export;
            if (arg.length > 1 && arg[1].equals(Constants.CAT)) {
                certInfo.cat = Category.valueOf(arg[2]);
            }

            if (arg.length > 3 && arg[3].equalsIgnoreCase(Constants.CA)) {
                certInfo.type = Type.ca;
                if (arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                    certInfo.serialno = arg[5];
                    if (arg[6].equalsIgnoreCase(Constants.FILENAME)) {
                        certInfo.tofile = arg[7];
                        if (arg[8].equalsIgnoreCase(Constants.FORMAT)) {
                            certInfo.format = Format.valueOf(arg[9]);
                        }
                    }
                }
            } else if (arg.length > 3 && arg[3].equalsIgnoreCase(Constants.ROOTCA)) {
                certInfo.type = Type.rootca;
                if (arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                    certInfo.serialno = arg[5];
                    if (arg[6].equalsIgnoreCase(Constants.FILENAME)) {
                        certInfo.tofile = arg[7];
                        if (arg[8].equalsIgnoreCase(Constants.FORMAT)) {
                            certInfo.format = Format.valueOf(arg[9]);
                        }
                    }
                }
            } else if (arg.length > 3 && arg[3].equalsIgnoreCase(Constants.CERTIFICATE)) {
                certInfo.type = Type.certificate;
                if (arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                    certInfo.serialno = arg[5];
                    if (arg[6].equalsIgnoreCase(Constants.FILENAME)) {
                        certInfo.tofile = arg[7];
                        if (arg[8].equalsIgnoreCase(Constants.FORMAT)) {
                            certInfo.format = Format.valueOf(arg[9]);
                        }
                    }
                }
            }
        }

    }

}
