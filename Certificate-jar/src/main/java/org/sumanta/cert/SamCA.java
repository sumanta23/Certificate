package org.sumanta.cert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.sumanta.bean.CA;
import org.sumanta.bean.RootCA;
import org.sumanta.cli.Category;
import org.sumanta.cli.Format;
import org.sumanta.cli.Type;
import org.sumanta.persistence.PersistenceManager;
import org.sumanta.rest.api.Content;
import org.sumanta.rest.api.ContentHolder;
import org.sumanta.to.ToJKS;
import org.sumanta.to.ToP12;
import org.sumanta.util.DeSerializer;
import org.sumanta.util.Serializer;

public class SamCA {

    @Inject
    private PersistenceManager persistenceManager;

    private KeyPair caKeyPair;
    private X509Certificate caCertificate;

    private KeyPair issuedKeyPair;
    private X509Certificate issuedCertificate;

    @Inject
    CertUtils certUtils;

    @Inject
    KeypairGenerator keypairGenerator;

    @Inject
    IssueCertificate certificateIssuer;

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public SamCA(X509Certificate caCert, KeyPair caKeypair) {
        this.caCertificate = caCert;
        this.caKeyPair = caKeypair;
    }

    public SamCA() {
    }

    /**
     * Initiates a new certificate signing authority Attempts to load a CA cert & key from filenames provided otherwise generates new ones
     * 
     * @param caCertFile
     *            A valid PEM encoded X.509 CA certificate
     * @param caKeyFile
     *            A valid PEM encoded RSA private key
     */
    /*
     * public SamCA(String caCertFile, String caKeyFile) {
     * 
     * // Check to see if there is a valid CA key - or generate one if not
     * 
     * try { this.caKeyPair = certUtils.loadKeyPairFromFile(caKeyFile); } catch (IOException e) { e.printStackTrace(); }
     * 
     * if (this.caKeyPair == null) { logger.info("Generating & saving new CA key: " + caKeyFile); try { this.caKeyPair = keypairGenerator.generateKeyPairAndSaveToFile(caKeyFile); } catch (Exception e)
     * { logger.info("Could not generate new CA key: " + e.getMessage()); return; } } else { logger.info("Loaded existing CA key: " + caKeyFile); }
     * 
     * // Check to see if there is a valid CA certificate - or generate one if // not try { this.caCertificate = certUtils.loadCertificateFromFile(caCertFile); } catch (IOException e) {
     * e.printStackTrace(); }
     * 
     * if (this.caCertificate == null) { logger.info("Generating & saving new CA certificate: " + caCertFile); try { this.caCertificate = SamCA.generateCACertificateAndSaveToFile(caCertFile,
     * caKeyPair); } catch (Exception e) { logger.info("Could not generate new CA certificate: " + e.getMessage()); return; } } else { logger.info("Loaded existing CA certificate: " + caCertFile); }
     * 
     * logger.info("CA: " + caCertificate.getSubjectDN());
     * 
     * }
     */
    public KeyPair getIssuedKeyPair() {
        return issuedKeyPair;
    }

    public X509Certificate getIssuedCertificate() {
        return issuedCertificate;
    }

    /**
     * Generates a valid CA certificate and saves it in PEM format to the specified filename
     * 
     * @param filename
     *            The filename to write out a CA certificate in PEM format
     * @param keyPair
     *            A private/public {@link KeyPair} to sign the CA certificate with
     * @return The generated certificate
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    /*
     * public static X509Certificate generateCACertificateAndSaveToFile(String filename, KeyPair keyPair) throws NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException,
     * InvalidKeyException, SignatureException, IOException {
     * 
     * X509Certificate certificate = generateV1Certificate(keyPair, "hj", 4);
     * 
     * final Writer writer = new FileWriter(filename); final PEMWriter pemWriter = new PEMWriter(writer); pemWriter.writeObject(certificate); pemWriter.close();
     * 
     * return certificate;
     * 
     * }
     */

    /**
     * @param serialno
     * @return
     * @throws Exception
     */
    public Certificate[] exportCertificateChain(String serialno) throws Exception {
        ArrayList<X509Certificate> cert = new ArrayList<X509Certificate>();
        X509Certificate certificate = null;
        Object obj = null;
        do {
            obj = getCertificateBySerialNo(serialno);

            if (obj instanceof org.sumanta.bean.Certificate) {
                certificate = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(((org.sumanta.bean.Certificate) obj).getCertificate()));
                serialno = ((org.sumanta.bean.Certificate) obj).getIssuerserailno();
            }
            if (obj instanceof org.sumanta.bean.CA) {
                certificate = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(((org.sumanta.bean.CA) obj).getCertificate()));
                serialno = ((org.sumanta.bean.CA) obj).getIssuerserailno();
            }
            if (obj instanceof org.sumanta.bean.RootCA) {
                certificate = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(((org.sumanta.bean.RootCA) obj).getCertificate()));
                // logger.info(((X509Certificate)
                // certificate).getSubjectDN().toString());
            }
            cert.add(certificate);
        } while (!certificate.getIssuerDN().equals(certificate.getSubjectDN()));

        Certificate[] ret = new Certificate[cert.size()];
        int i = 0;
        for (Certificate certificate2 : cert) {
            ret[i] = certificate2;
            i++;
        }
        return ret;
    }

    /**
     * @param type
     * @param serialNo
     * @param file
     * @param cat
     */
    public String exportCertificateToFile(Type type, String serialNo, String file, Category cat, Format format) {
        X509Certificate cert = null;
        KeyPair key = null;
        String fileid = null;
        try {
            String query = "";
            if (serialNo != null && serialNo.equals("")) {
                query = query + "where serialno='" + serialNo + "'";
            }
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("serialno", serialNo);
            if (type.equals(Type.ca)) {

                List l = persistenceManager.findEntitiesByAttributes(CA.class, attributes);
                if (l.size() != 0) {
                    Iterator it = l.listIterator();
                    while (it.hasNext()) {
                        CA object = (CA) it.next();
                        cert = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(object.getCertificate()));
                        key = (KeyPair) DeSerializer.deserialize(new ByteArrayInputStream(object.getKeypair()));
                    }
                }
            }

            if (type.equals(Type.rootca)) {

                List l = persistenceManager.findEntitiesByAttributes(RootCA.class, attributes);
                Iterator it = l.listIterator();
                while (it.hasNext()) {
                    RootCA object = (RootCA) it.next();
                    cert = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(object.getCertificate()));
                    key = (KeyPair) DeSerializer.deserialize(new ByteArrayInputStream(object.getKeypair()));
                }
            }

            if (type.equals(Type.certificate)) {

                List l = persistenceManager.findEntitiesByAttributes(org.sumanta.bean.Certificate.class, attributes);
                Iterator it = l.listIterator();
                while (it.hasNext()) {
                    org.sumanta.bean.Certificate object = (org.sumanta.bean.Certificate) it.next();
                    cert = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(object.getCertificate()));
                    key = (KeyPair) DeSerializer.deserialize(new ByteArrayInputStream(object.getKeypair()));
                }
            }
            if (Category.keystore == cat) {
                final Certificate[] certificate = { cert };
                if (Format.p12 == format) {
                    ToP12.toP12withPrivateKey("key", "secret", certificate, key.getPrivate(), "key.p12");
                } else {
                    File f=ToJKS.toJKSKeyStore(certificate, key.getPrivate(), "secret", "key", file);
                    fileid=saveFileToFileHolder(f, file);
                }
            } else if (Category.truststore == cat) {
                if (Format.jks == format) {
                    ToJKS.toJKSTrustStore(exportCertificateChain(cert.getSerialNumber().toString()), "secret", file);
                }
                // ToP12.toP12withPrivateKey("key", "secret",
                // SamCA.exportCertificateChain(cert.getSerialNumber().toString()),
                // key.getPrivate(), "key.p12");
            } else if (Category.certificate == cat) {
                fileid = saveCertificateToFileHolder(cert, file);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileid;
    }

    

    /**
     * @param certificate
     * @param name
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    public String saveFileToFileHolder(File certificate, String name) throws NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException, InvalidKeyException,
            SignatureException, IOException {
        String key = "http" + System.currentTimeMillis();
        Map<String, Content> holder = ContentHolder.getInstance().getHolder();
        
        byte[] f = Serializer.serialize(certificate);
        
        FileInputStream fileInputStream=null;
        byte[] bFile = new byte[(int) certificate.length()];
        
        try {
            //convert file into array of bytes
	    fileInputStream = new FileInputStream(certificate);
	    fileInputStream.read(bFile);
	    fileInputStream.close();
        }catch(Exception e){}
        
        Content content=new Content("jks",bFile);
        holder.put(key, content);
        return key;
    }
    
    
    /**
     * @param certificate
     * @param name
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    public String saveCertificateToFileHolder(Certificate certificate, String name) throws NoSuchAlgorithmException, CertificateEncodingException, NoSuchProviderException, InvalidKeyException,
            SignatureException, IOException {
        System.out.println(certificate.getEncoded().toString());
        String key = "http" + System.currentTimeMillis();
        Map<String, Content> holder = ContentHolder.getInstance().getHolder();
        byte[] f = certificate.getEncoded();
        Content content=new Content("crt",f);
        holder.put(key, content);
        return key;
    }

    public Object getCertificateBySerialNo(String serialNo) {
        Object object = null;
        List l = null;
        try {
            String query = "";
            if (serialNo != null && !serialNo.equals("")) {
                query = query + "where serialno='" + serialNo + "'";
            }
            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("serialno", serialNo);

            l = persistenceManager.findEntitiesByAttributes(org.sumanta.bean.Certificate.class, attributes);

            if (l.isEmpty()) {
                l = persistenceManager.findEntitiesByAttributes(CA.class, attributes);
            }
            if (l.isEmpty()) {
                l = persistenceManager.findEntitiesByAttributes(RootCA.class, attributes);
            }

            if (l.get(0) instanceof org.sumanta.bean.Certificate) {
                object = (org.sumanta.bean.Certificate) l.get(0);
            } else if (l.get(0) instanceof org.sumanta.bean.CA) {
                object = (org.sumanta.bean.CA) l.get(0);
            } else if (l.get(0) instanceof org.sumanta.bean.RootCA) {
                object = (org.sumanta.bean.RootCA) l.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (l != null) {
                l.clear();
            }
            return object;
        }
    }

    /**
     * @param serialNo
     * @return
     */
    public X509Certificate getCACertificateBySerialNo(String serialNo) {
        X509Certificate cert = null;
        try {
            String query = "";
            if (serialNo != null && serialNo.equals("")) {
                query = query + "where serialno='" + serialNo + "'";
            }

            /*
             * RootCAManager rcmngr = new RootCAManager(); List l = cmngr.viewCAs(query);
             */
            Map<String, Object> att = new HashMap<String, Object>();
            att.put("serialno", serialNo);
            List l = persistenceManager.findEntitiesByAttributes(org.sumanta.bean.CA.class, att);
            if (l.size() != 0) {
                Iterator it = l.listIterator();
                while (it.hasNext()) {
                    CA object = (CA) it.next();
                    cert = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(object.getCertificate()));
                }
            } else {
                l = persistenceManager.findEntitiesByAttributes(org.sumanta.bean.RootCA.class, att);
                Iterator it = l.listIterator();
                while (it.hasNext()) {
                    RootCA object = (RootCA) it.next();
                    cert = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(object.getCertificate()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return cert;
        }
    }

    /**
     * @param serialNo
     * @return
     */
    public KeyPair getCAKeyPairBySerialNo(String serialNo) {
        KeyPair keypair = null;
        try {
            String query = "";
            if (serialNo != null && serialNo.equals("")) {
                query = query + "where serialno='" + serialNo + "'";
            }

            Map<String, Object> attributes = new HashMap<String, Object>();
            attributes.put("serialno", serialNo);
            List l = persistenceManager.findEntitiesByAttributes(CA.class, attributes);
            if (l.size() != 0) {
                final Iterator it = l.listIterator();
                while (it.hasNext()) {
                    CA object = (CA) it.next();
                    keypair = (KeyPair) DeSerializer.deserialize(new ByteArrayInputStream(object.getKeypair()));
                }
            } else {
                l = persistenceManager.findEntitiesByAttributes(RootCA.class, attributes);
                Iterator it = l.listIterator();
                while (it.hasNext()) {
                    RootCA object = (RootCA) it.next();
                    keypair = (KeyPair) DeSerializer.deserialize(new ByteArrayInputStream(object.getKeypair()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return keypair;
        }
    }

    /**
     * @param issuer
     * @param commonName
     * @param validityinDays
     * @param keypair
     */
    public void createCA(String issuer, String commonName, long validityinDays, KeyPair keypair) {
        try {
            if (keypair == null) {
                keypair = keypairGenerator.generateRSAKeyPair();
            }
            X509Certificate rootca = getCACertificateBySerialNo(issuer);
            KeyPair rootkKeyPair = getCAKeyPairBySerialNo(issuer);
            // PKCS10CertificationRequest cr = SamCA.generateCSR(keypair, "CN="
            // + commonName);

            SamCA ca = new SamCA((X509Certificate) rootca, rootkKeyPair);
            X509Certificate issedcert = certificateIssuer.issueV3Certificate(rootca, rootkKeyPair, keypair, "CN=" + commonName, (int) validityinDays, SignatureAlgo.SHA256withRSA,
                    KeyPurposeId.id_kp_ipsecUser);

            CA myca = new CA();
            myca.setIssuer(issedcert.getIssuerDN().toString());
            myca.setSerialno(issedcert.getSerialNumber().toString());
            myca.setIssuerserailno(issuer);
            myca.setDn(issedcert.getSubjectDN().toString());
            myca.setCertificate(Serializer.serialize(issedcert));
            myca.setKeypair(Serializer.serialize(keypair));

            persistenceManager.createEntity(myca);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param commonName
     * @param days
     */
    public void createRootCA(String commonName, double days) {
        try {
            final KeyPair keyPair = keypairGenerator.generateRSAKeyPair();
            X509Certificate certificate = certificateIssuer.issueV1Certificate(keyPair, commonName, days);

            RootCA rootca = new RootCA();
            rootca.setIssuer(certificate.getIssuerDN().toString());
            rootca.setSerialno(certificate.getSerialNumber().toString());
            rootca.setDn(certificate.getSubjectDN().toString());
            rootca.setCertificate(Serializer.serialize(certificate));
            rootca.setKeypair(Serializer.serialize(keyPair));

            persistenceManager.createEntity(rootca);
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param type
     * @param commonName
     * @param serialNo
     * @throws Exception
     */
    public String listCertificate(Type type, String commonName, String serialNo) throws Exception {
        X509Certificate cert = null;
        StringBuffer result = new StringBuffer();
        String query = "";
        Map<String, Object> attributes = new HashMap<String, Object>();

        if (commonName != null && !commonName.isEmpty()) {
            attributes.put("dn", commonName);
        }
        if (serialNo != null && !serialNo.isEmpty()) {
            attributes.put("serialno", serialNo);
        }

        if (type.equals(Type.ca)) {

            List l = persistenceManager.findEntitiesByAttributes(CA.class, attributes);
            for (Object object : l) {
                CA ca = (CA) object;
                X509Certificate c = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(ca.getCertificate()));
                result.append(ca.getId());
                result.append("\t");
                result.append(ca.getDn());
                result.append("\t");
                result.append(c.getSerialNumber());
                result.append("\t");
                result.append(c.getIssuerDN());
                result.append("\t");
                result.append(c.getNotBefore());
                result.append("\t");
                result.append(c.getNotAfter());
                result.append("\n");
            }
        }
        if (type.equals(Type.rootca)) {
            // query = "select * from RootCA ";

            final List l = persistenceManager.findEntitiesByAttributes(RootCA.class, attributes);
            for (Object object : l) {
                RootCA rca = (RootCA) object;
                X509Certificate c = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(rca.getCertificate()));
                result.append(rca.getId());
                result.append("\t");
                result.append(rca.getDn());
                result.append("\t");
                result.append(c.getSerialNumber());
                result.append("\t");
                result.append(c.getIssuerDN());
                result.append("\t");
                result.append(c.getNotBefore());
                result.append("\t");
                result.append(c.getNotAfter());
                result.append("\n");
            }
        }
        if (type.equals(Type.certificate)) {
            List l = persistenceManager.findEntitiesByAttributes(org.sumanta.bean.Certificate.class, attributes);
            for (Object object : l) {
                org.sumanta.bean.Certificate certificate = (org.sumanta.bean.Certificate) object;
                X509Certificate c = (X509Certificate) DeSerializer.deserialize(new ByteArrayInputStream(certificate.getCertificate()));
                result.append(certificate.getId());
                result.append("\t");
                result.append(certificate.getDn());
                result.append("\t");
                result.append(c.getSerialNumber());
                result.append("\t");
                result.append(c.getIssuerDN());
                result.append("\t");
                result.append(c.getNotBefore());
                result.append("\t");
                result.append(c.getNotAfter());
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * @param issuer
     * @param commonName
     * @param validityinDays
     * @param keypair
     */
    public void createCertificate(String issuer, String commonName, long validityinDays, KeyPair keypair) {

        try {
            if (keypair == null) {
                keypair = keypairGenerator.generateRSAKeyPair();
            }
            X509Certificate rootca = getCACertificateBySerialNo(issuer);
            KeyPair rootkKeyPair = getCAKeyPairBySerialNo(issuer);

            final SamCA ca = new SamCA((X509Certificate) rootca, rootkKeyPair);
            X509Certificate certificate = certificateIssuer.issueV3Certificate(rootca, rootkKeyPair, keypair, "CN=" + commonName, (int) validityinDays, "SHA256WithRSAEncryption",
                    KeyPurposeId.anyExtendedKeyUsage);
            KeyPair kpair = keypair;
            X509Certificate issedcert = certificate;
            org.sumanta.bean.Certificate mycert = new org.sumanta.bean.Certificate();
            mycert.setIssuer(issedcert.getIssuerDN().toString());
            mycert.setSerialno(issedcert.getSerialNumber().toString());
            mycert.setIssuerserailno(issuer);
            mycert.setDn(issedcert.getSubjectDN().toString());
            mycert.setCertificate(Serializer.serialize(issedcert));
            mycert.setKeypair(Serializer.serialize(kpair));

            persistenceManager.createEntity(mycert);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
