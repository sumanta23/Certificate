package org.sumanta.cli;

import java.io.PrintStream;


//interface for parcing cli command
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
	
	public static void help() {
		final PrintStream out = System.out;
		
		out.println("-create rootca -validity <days> -cn <>");
		out.println("-create ca|certificate -issuer <> -validity <days> -cn <>");
		
		out.println("-list rootca|ca|certificate [-cn <>] [-serial <>]");
	
		out.println("-export -cat <keystore|truststore|certificate> rootca|ca|certificate -serial <> -tofile <> -format pem|crt|jks|der|p12");
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
		if (arg[0].equalsIgnoreCase("-create")) {
			opt = Operation.create;
			if (arg[1].equalsIgnoreCase("ca")) {
				type = Type.ca;
				if (arg[2].equalsIgnoreCase("-issuer")) {
					issuer = arg[3];
					if (arg[4].equalsIgnoreCase("-validity")) {
						validity = Integer.parseInt(arg[5]);
						if (arg[6].equalsIgnoreCase("-cn")) {
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
				if (arg[1].equalsIgnoreCase("rootca")) {
					type = Type.rootca;
					if (arg[2].equalsIgnoreCase("-validity")) {
						validity = Integer.parseInt(arg[3]);
						if (arg[4].equalsIgnoreCase("-cn")) {
							commonName = arg[5];
						} else {
							help();
						}
					} else {
						help();
					}
				}

				else {
					if (arg[1].equalsIgnoreCase("certificate")) {
						type = Type.certificate;
						if (arg[2].equalsIgnoreCase("-issuer")) {
							issuer = arg[3];
							if (arg[4].equalsIgnoreCase("-validity")) {
								validity = Integer.parseInt(arg[5]);
								if (arg[6].equalsIgnoreCase("-cn")) {
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
		if (arg[0].equalsIgnoreCase("-list")) {
			opt = Operation.list;
			if (arg[1].equalsIgnoreCase("ca")) {
				type = Type.ca;
				if (arg.length > 2 && arg[2].equalsIgnoreCase("-cn")) {
					commonName = arg[3];
					if (arg.length > 4 && arg[4].equalsIgnoreCase("-serialno")) {
						serialno = arg[5];
					}
				}
				if (arg.length > 2 && arg[2].equalsIgnoreCase("-serialno")) {
					serialno = arg[3];
					if (arg.length > 4 && arg[4].equalsIgnoreCase("-cn")) {
						commonName = arg[5];
					}
				}
			} else if (arg[1].equalsIgnoreCase("rootca")) {
				type = Type.rootca;
				if (arg.length > 2 && arg[2].equalsIgnoreCase("-cn")) {
					commonName = arg[3];
					if (arg.length > 4 && arg[4].equalsIgnoreCase("-serialno")) {
						serialno = arg[5];
					}
				}
				if (arg.length > 2 && arg[2].equalsIgnoreCase("-serialno")) {
					serialno = arg[3];
					if (arg.length > 4 && arg[4].equalsIgnoreCase("-cn")) {
						commonName = arg[5];
					}
				}
			} else if (arg[1].equalsIgnoreCase("certificate")) {
				type = Type.certificate;
				if (arg[1].equalsIgnoreCase("ca")) {
					type = Type.ca;
					if (arg.length > 2 && arg[2].equalsIgnoreCase("-cn")) {
						commonName = arg[3];
						if (arg.length > 3
								&& arg[4].equalsIgnoreCase("-serialno")) {
							serialno = arg[5];
						}
					}
					if (arg.length > 2 && arg[2].equalsIgnoreCase("-serialno")) {
						serialno = arg[3];
						if (arg.length > 4 && arg[4].equalsIgnoreCase("-cn")) {
							commonName = arg[5];
						}
					}
				}
			}
		}

		// export
		if (arg[0].equalsIgnoreCase("-export")) {
			opt = Operation.export;
			if(arg.length>1 && arg[1].equals("-cat")){
				cat=Category.valueOf(arg[2]);
			}
			
			if (arg.length > 3 && arg[3].equalsIgnoreCase("ca")) {
				type = Type.ca;
				if (arg[4].equalsIgnoreCase("-serialno")) {
					serialno = arg[5];
					if (arg[6].equalsIgnoreCase("-tofile")) {
						tofile = arg[7];
						if (arg[8].equalsIgnoreCase("-format")) {
							format = Format.valueOf(arg[9]);
						}
					}
				}
			} else if (arg.length > 3 && arg[3].equalsIgnoreCase("rootca")) {
				type = Type.rootca;
				if (arg[4].equalsIgnoreCase("-serialno")) {
					serialno = arg[5];
					if (arg[6].equalsIgnoreCase("-tofile")) {
						tofile = arg[7];
						if (arg[8].equalsIgnoreCase("-format")) {
							format = Format.valueOf(arg[9]);
						}
					}
				}
			} else if (arg.length > 3 && arg[3].equalsIgnoreCase("certificate")) {
				type = Type.certificate;
				if (arg[4].equalsIgnoreCase("-serialno")) {
					serialno = arg[5];
					if (arg[6].equalsIgnoreCase("-tofile")) {
						tofile = arg[7];
						if (arg[8].equalsIgnoreCase("-format")) {
							format = Format.valueOf(arg[9]);
						}
					}
				}
			}
		}

	}

}
