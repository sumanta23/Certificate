
from OpenSSL import crypto, SSL
from socket import gethostname
from pprint import pprint
from time import gmtime, mktime
from os.path import exists, join
import sys, getopt

def main(argv):
    try:
        arg = argv[1]
    except Exception, e:
        print e
        sys.exit(2)
    createcert(arg)
   
def createcert(cert_dir):
    cn='ssltest'
    CERT_FILE = "%s.crt" % cn
    KEY_FILE = "%s.key" % cn
    k = crypto.PKey()
    k.generate_key(crypto.TYPE_RSA, 2048)
    cert = crypto.X509()
    cert.get_subject().CN = cn
    cert.set_serial_number(1000)
    cert.gmtime_adj_notBefore(0)
    cert.gmtime_adj_notAfter(315360000)
    cert.set_issuer(cert.get_subject())
    cert.set_pubkey(k)
    cert.sign(k, 'sha256')
    C_F = join(cert_dir, CERT_FILE)
    K_F = join(cert_dir, KEY_FILE)
    open(C_F, "wt").write(crypto.dump_certificate(crypto.FILETYPE_PEM, cert))
    open(K_F, "wt").write(crypto.dump_privatekey(crypto.FILETYPE_PEM, k))


if __name__ == "__main__":
    main(sys.argv)
