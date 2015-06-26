package org.sumanta.cert;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

/**
 * @author Sumanta
 *
 */
public class IssueCertificate {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private KeyPair issuedKeyPair;
    private KeyPair caKeyPair;
    private X509Certificate caCertificate;

    KeypairGenerator keypairGenerator = new KeypairGenerator();

    CSRGenerator csrGenerator = new CSRGenerator();

    /**
     * Generates a v1 certificate - suitable for a CA with no usage restrictions
     * 
     * @param pair
     *            A public/private KeyPair to use for signing the CA certificate
     * @return A valid v1 X.509 certificate
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     */
    public X509Certificate issueV1Certificate(final KeyPair pair, final String commonName, final double days) throws InvalidKeyException, NoSuchProviderException, SignatureException,
            NoSuchAlgorithmException, CertificateEncodingException {

        final X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X500Principal("CN=" + commonName));
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24) * (long) days));
        certGen.setSubjectDN(new X500Principal("CN=" + commonName));
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        return certGen.generate(pair.getPrivate(), "BC");

    }

    /**
     * Generates an SSL certificate
     * 
     * @param cn
     *            Common name for certificate (eg: blah.mydomain.com)
     * @param days
     *            Number of days the certificate should be valid for
     * @param purposeId
     *            A {@link KeyPurposeId} that defines what the certificate can be used for
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public X509Certificate issueV3Certificate(final X509Certificate caCertificate, final KeyPair rootKeyPair, final KeyPair keypair, final String cn, final int days, final String signatureAlgorithm,
            final KeyPurposeId purposeId) throws Exception {

        if (keypair != null) {
            this.issuedKeyPair = keypair;
        } else {
            this.issuedKeyPair = keypairGenerator.generateRSAKeyPair();
        }

        final PKCS10CertificationRequest request = csrGenerator.generateCSR(issuedKeyPair, cn);

        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(caCertificate.getSubjectX500Principal());
        certGen.setNotBefore(new Date(System.currentTimeMillis()));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * days)));
        certGen.setSubjectDN(request.getCertificationRequestInfo().getSubject());
        certGen.setPublicKey(issuedKeyPair.getPublic());
        certGen.setSignatureAlgorithm(signatureAlgorithm);

        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCertificate));
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(request.getPublicKey("BC")));
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(purposeId));

        final ASN1Set attributes = request.getCertificationRequestInfo().getAttributes();

        if (attributes != null) {
            for (int i = 0; i != attributes.size(); i++) {
                final org.bouncycastle.asn1.pkcs.Attribute attr = org.bouncycastle.asn1.pkcs.Attribute.getInstance(attributes.getObjectAt(i));

                X509Extensions extensions;
                if (attr.getAttrType().equals(extensions = X509Extensions.getInstance(attr.getAttrValues().getObjectAt(0))))
                    ;

                final Enumeration e = extensions.oids();
                while (e.hasMoreElements()) {
                    final DERObjectIdentifier oid = (DERObjectIdentifier) e.nextElement();
                    final X509Extension ext = extensions.getExtension(oid);

                    certGen.addExtension(oid, ext.isCritical(), ext.getValue().getOctets());
                }
            }
        }

        final X509Certificate issuedCertificate = certGen.generate(rootKeyPair.getPrivate());

        return issuedCertificate;
    }

    @SuppressWarnings("deprecation")
    public X509Certificate signCSR(final PKCS10CertificationRequest cr, final int days, final KeyPurposeId purposeId) throws Exception {
        this.issuedKeyPair = keypairGenerator.generateRSAKeyPair();

        final X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(caCertificate.getSubjectX500Principal());
        certGen.setNotBefore(new Date(System.currentTimeMillis()));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * days)));
        certGen.setSubjectDN(cr.getCertificationRequestInfo().getSubject());
        certGen.setPublicKey(cr.getPublicKey("BC"));
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifierStructure(caCertificate));
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifierStructure(cr.getPublicKey("BC")));
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
        certGen.addExtension(X509Extensions.ExtendedKeyUsage, true, new ExtendedKeyUsage(purposeId));

        final ASN1Set attributes = cr.getCertificationRequestInfo().getAttributes();

        if (attributes != null) {
            for (int i = 0; i != attributes.size(); i++) {
                final org.bouncycastle.asn1.pkcs.Attribute attr = org.bouncycastle.asn1.pkcs.Attribute.getInstance(attributes.getObjectAt(i));
                X509Extensions extensions;
                if (attr.getAttrType().equals(extensions = X509Extensions.getInstance(attr.getAttrValues().getObjectAt(0))))
                    ;

                Enumeration e = extensions.oids();
                while (e.hasMoreElements()) {
                    final DERObjectIdentifier oid = (DERObjectIdentifier) e.nextElement();
                    final X509Extension ext = extensions.getExtension(oid);

                    certGen.addExtension(oid, ext.isCritical(), ext.getValue().getOctets());
                }
            }
        }

        return certGen.generate(caKeyPair.getPrivate());
    }

}
