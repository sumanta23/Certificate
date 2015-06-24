package org.sumanta.cli;

import org.sumanta.util.Constants;

public abstract class CertAdminInterface {

    Type type;
    Operation opt;
    String issuer = "";
    long validity = 100;
    String commonName = "";
    String serialno = "";
    String tofile = "";
    Category cat;
    Format format;

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
            opt = Operation.create;
            if (arg[1].equalsIgnoreCase(Constants.CA)) {
                type = Type.ca;
                if (arg[2].equalsIgnoreCase(Constants.ISSUER)) {
                    issuer = arg[3];
                    if (arg[4].equalsIgnoreCase(Constants.VALIDITY)) {
                        validity = Integer.parseInt(arg[5]);
                        if (arg[6].equalsIgnoreCase(Constants.CN)) {
                            commonName = arg[7];
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
                    type = Type.rootca;
                    if (arg[2].equalsIgnoreCase(Constants.VALIDITY)) {
                        validity = Integer.parseInt(arg[3]);
                        if (arg[4].equalsIgnoreCase(Constants.CN)) {
                            commonName = arg[5];
                        } else {
                            help();
                        }
                    } else {
                        help();
                    }
                }

                else {
                    if (arg[1].equalsIgnoreCase(Constants.CERTIFICATE)) {
                        type = Type.certificate;
                        if (arg[2].equalsIgnoreCase(Constants.ISSUER)) {
                            issuer = arg[3];
                            if (arg[4].equalsIgnoreCase(Constants.VALIDITY)) {
                                validity = Integer.parseInt(arg[5]);
                                if (arg[6].equalsIgnoreCase(Constants.CN)) {
                                    commonName = arg[7];
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
            opt = Operation.list;
            if (arg[1].equalsIgnoreCase(Constants.CA)) {
                type = Type.ca;
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.CN)) {
                    commonName = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                        serialno = arg[5];
                    }
                }
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.SERIALNO)) {
                    serialno = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.CN)) {
                        commonName = arg[5];
                    }
                }
            } else if (arg[1].equalsIgnoreCase(Constants.ROOTCA)) {
                type = Type.rootca;
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.CN)) {
                    commonName = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                        serialno = arg[5];
                    }
                }
                if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.SERIALNO)) {
                    serialno = arg[3];
                    if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.CN)) {
                        commonName = arg[5];
                    }
                }
            } else if (arg[1].equalsIgnoreCase(Constants.CERTIFICATE)) {
                type = Type.certificate;
                if (arg[1].equalsIgnoreCase(Constants.CA)) {
                    type = Type.ca;
                    if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.CN)) {
                        commonName = arg[3];
                        if (arg.length > 3 && arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                            serialno = arg[5];
                        }
                    }
                    if (arg.length > 2 && arg[2].equalsIgnoreCase(Constants.SERIALNO)) {
                        serialno = arg[3];
                        if (arg.length > 4 && arg[4].equalsIgnoreCase(Constants.CN)) {
                            commonName = arg[5];
                        }
                    }
                }
            }
        }

        // export
        if (arg[0].equalsIgnoreCase(Constants.EXPORT)) {
            opt = Operation.export;
            if (arg.length > 1 && arg[1].equals(Constants.CAT)) {
                cat = Category.valueOf(arg[2]);
            }

            if (arg.length > 3 && arg[3].equalsIgnoreCase(Constants.CA)) {
                type = Type.ca;
                if (arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                    serialno = arg[5];
                    if (arg[6].equalsIgnoreCase(Constants.TOFILE)) {
                        tofile = arg[7];
                        if (arg[8].equalsIgnoreCase(Constants.FORMAT)) {
                            format = Format.valueOf(arg[9]);
                        }
                    }
                }
            } else if (arg.length > 3 && arg[3].equalsIgnoreCase(Constants.ROOTCA)) {
                type = Type.rootca;
                if (arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                    serialno = arg[5];
                    if (arg[6].equalsIgnoreCase(Constants.TOFILE)) {
                        tofile = arg[7];
                        if (arg[8].equalsIgnoreCase(Constants.FORMAT)) {
                            format = Format.valueOf(arg[9]);
                        }
                    }
                }
            } else if (arg.length > 3 && arg[3].equalsIgnoreCase(Constants.CERTIFICATE)) {
                type = Type.certificate;
                if (arg[4].equalsIgnoreCase(Constants.SERIALNO)) {
                    serialno = arg[5];
                    if (arg[6].equalsIgnoreCase(Constants.TOFILE)) {
                        tofile = arg[7];
                        if (arg[8].equalsIgnoreCase(Constants.FORMAT)) {
                            format = Format.valueOf(arg[9]);
                        }
                    }
                }
            }
        }

    }

}
