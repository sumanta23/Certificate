package org.sumanta.cert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.sumanta.bean.CA;
import org.sumanta.bean.RootCA;
import org.sumanta.cli.Category;
import org.sumanta.cli.Format;
import org.sumanta.cli.Type;
import org.sumanta.db.util.Constant;
import org.sumanta.dbhandler.CAManager;
import org.sumanta.dbhandler.CertificateManager;
import org.sumanta.dbhandler.RootCAManager;

public class SamCA {

	private static final Logger LOG = Logger.getLogger(SamCA.class.getName());
	private static PrintStream out = System.out;
	private KeyPair caKeyPair;
	private X509Certificate caCertificate;

	private KeyPair issuedKeyPair;
	private X509Certificate issuedCertificate;

	static {
		// Load BouncyCastle security provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public SamCA(X509Certificate caCert, KeyPair caKeypair) {
		this.caCertificate = caCert;
		this.caKeyPair = caKeypair;
	}

	/**
	 * Initiates a new certificate signing authority Attempts to load a CA cert
	 * & key from filenames provided otherwise generates new ones
	 * 
	 * @param caCertFile
	 *            A valid PEM encoded X.509 CA certificate
	 * @param caKeyFile
	 *            A valid PEM encoded RSA private key
	 */
	public SamCA(String caCertFile, String caKeyFile) {

		// Check to see if there is a valid CA key - or generate one if not

		try {
			this.caKeyPair = SamCA.loadKeyPairFromFile(caKeyFile);
		} catch (IOException e) {
		}

		if (this.caKeyPair == null) {
			out.println("Generating & saving new CA key: " + caKeyFile);
			try {
				this.caKeyPair = SamCA.generateKeyPairAndSaveToFile(caKeyFile);
			} catch (Exception e) {
				out.println("Could not generate new CA key: " + e.getMessage());
				return;
			}
		} else {
			out.println("Loaded existing CA key: " + caKeyFile);
		}

		// Check to see if there is a valid CA certificate - or generate one if
		// not
		try {
			this.caCertificate = SamCA.loadCertificateFromFile(caCertFile);
		} catch (IOException e) {
		}

		if (this.caCertificate == null) {
			out.println("Generating & saving new CA certificate: " + caCertFile);
			try {
				this.caCertificate = SamCA.generateCACertificateAndSaveToFile(
						caCertFile, caKeyPair);
			} catch (Exception e) {
				out.println("Could not generate new CA certificate: "
						+ e.getMessage());
				return;
			}
		} else {
			out.println("Loaded existing CA certificate: " + caCertFile);
		}

		out.println("CA: " + caCertificate.getSubjectDN());

	}

	/**
	 * Generates an RSA public/private KeyPair
	 * 
	 * @return Generated KeyPair
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	public static KeyPair generateRSAKeyPair() throws NoSuchProviderException,
			NoSuchAlgorithmException {
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA", "BC");
		kpGen.initialize(1024, new SecureRandom());
		return kpGen.generateKeyPair();
	}

	public static byte[] serialize(Object obj) {
		byte[] objectAsBytes = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			objectAsBytes = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return objectAsBytes;
		}
	}

	private static Object deserialize(InputStream stream) throws Exception {

		ObjectInputStream ois = new ObjectInputStream(stream);
		try {
			return ois.readObject();
		} finally {
			ois.close();
		}
	}

	/**
	 * Generate an SSL CSR
	 * 
	 * @param pair
	 *            KeyPair to use for the CSR
	 * @param cn
	 *            Common name for certificate (eg: blah.mydomain.com)
	 * @return Generated CSR object
	 * @throws Exception
	 */
	public static PKCS10CertificationRequest generateCSR(KeyPair pair, String cn)
			throws Exception {
		return new PKCS10CertificationRequest("SHA256withRSA",
				new X500Principal("CN=" + cn), pair.getPublic(), null,
				pair.getPrivate());
	}

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
	public static X509Certificate generateV1Certificate(KeyPair pair,
			String commonName, double days) throws InvalidKeyException,
			NoSuchProviderException, SignatureException,
			NoSuchAlgorithmException, CertificateEncodingException {

		X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setIssuerDN(new X500Principal("CN=" + commonName));
		certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
		certGen.setNotAfter(new Date(System.currentTimeMillis()
				+ (1000 * 60 * 60 * 24)*(long) days)); 
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
	 *            A {@link KeyPurposeId} that defines what the certificate can
	 *            be used for
	 * @throws Exception
	 */
	public void issueCertificate(KeyPair keypair, String cn, int days,
			KeyPurposeId purposeId) throws Exception {

		if (keypair != null) {
			this.issuedKeyPair = keypair;
		} else {
			this.issuedKeyPair = generateRSAKeyPair();
		}

		PKCS10CertificationRequest request = generateCSR(issuedKeyPair, cn);

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setIssuerDN(caCertificate.getSubjectX500Principal());
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis()
				+ (1000L * 60 * 60 * 24 * days)));
		certGen.setSubjectDN(request.getCertificationRequestInfo().getSubject());
		certGen.setPublicKey(request.getPublicKey("BC"));
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
				new AuthorityKeyIdentifierStructure(caCertificate));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
				new SubjectKeyIdentifierStructure(request.getPublicKey("BC")));
		certGen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(purposeId));

		ASN1Set attributes = request.getCertificationRequestInfo()
				.getAttributes();

		if (attributes != null) {
			for (int i = 0; i != attributes.size(); i++) {
				org.bouncycastle.asn1.pkcs.Attribute attr = org.bouncycastle.asn1.pkcs.Attribute
						.getInstance(attributes.getObjectAt(i));

				if (attr.getAttrType().equals(
						PKCSObjectIdentifiers.pkcs_9_at_extensionRequest)) {
					X509Extensions extensions = X509Extensions.getInstance(attr
							.getAttrValues().getObjectAt(0));

					Enumeration e = extensions.oids();
					while (e.hasMoreElements()) {
						DERObjectIdentifier oid = (DERObjectIdentifier) e
								.nextElement();
						final X509Extension ext = extensions.getExtension(oid);

						certGen.addExtension(oid, ext.isCritical(), ext
								.getValue().getOctets());
					}
				}
			}
		}

		this.issuedCertificate = certGen.generate(caKeyPair.getPrivate());

	}

	public KeyPair getIssuedKeyPair() {
		return issuedKeyPair;
	}

	public X509Certificate getIssuedCertificate() {
		return issuedCertificate;
	}

	public X509Certificate signCSR(PKCS10CertificationRequest cr, int days,
			KeyPurposeId purposeId) throws Exception {
		this.issuedKeyPair = generateRSAKeyPair();

		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setIssuerDN(caCertificate.getSubjectX500Principal());
		certGen.setNotBefore(new Date(System.currentTimeMillis()));
		certGen.setNotAfter(new Date(System.currentTimeMillis()
				+ (1000L * 60 * 60 * 24 * days)));
		certGen.setSubjectDN(cr.getCertificationRequestInfo().getSubject());
		certGen.setPublicKey(cr.getPublicKey("BC"));
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
				new AuthorityKeyIdentifierStructure(caCertificate));
		certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
				new SubjectKeyIdentifierStructure(cr.getPublicKey("BC")));
		certGen.addExtension(X509Extensions.BasicConstraints, true,
				new BasicConstraints(false));
		certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(
				KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,
				new ExtendedKeyUsage(purposeId));

		ASN1Set attributes = cr.getCertificationRequestInfo().getAttributes();

		if (attributes != null) {
			for (int i = 0; i != attributes.size(); i++) {
				org.bouncycastle.asn1.pkcs.Attribute attr = org.bouncycastle.asn1.pkcs.Attribute
						.getInstance(attributes.getObjectAt(i));

				if (attr.getAttrType().equals(
						PKCSObjectIdentifiers.pkcs_9_at_extensionRequest)) {
					X509Extensions extensions = X509Extensions.getInstance(attr
							.getAttrValues().getObjectAt(0));

					Enumeration e = extensions.oids();
					while (e.hasMoreElements()) {
						DERObjectIdentifier oid = (DERObjectIdentifier) e
								.nextElement();
						X509Extension ext = extensions.getExtension(oid);

						certGen.addExtension(oid, ext.isCritical(), ext
								.getValue().getOctets());
					}
				}
			}
		}

		return certGen.generate(caKeyPair.getPrivate());
	}

	/**
	 * Loads an X.509 certificate from file
	 * 
	 * @param filename
	 *            File to load from
	 * @return Valid X509Certificate or null
	 * @throws IOException
	 */
	public static X509Certificate loadCertificateFromFile(String filename)
			throws IOException {

		X509Certificate cert = null;

		try {
			final Reader reader = new FileReader(filename);
			final PEMReader pemReader = new PEMReader(reader);

			Object object;
			while ((object = pemReader.readObject()) != null) {
				if (object instanceof X509Certificate) {
					cert = (X509Certificate) object;
				}
			}

			reader.close();
			return cert;
		} catch (FileNotFoundException e) {
			return null;
		}

	}

	/**
	 * Loads a KeyPair from file
	 * 
	 * @param filename
	 *            File to load from
	 * @return Valid KeyPair or null
	 * @throws IOException
	 */
	public static KeyPair loadKeyPairFromFile(String filename)
			throws IOException {

		KeyPair keyPair = null;

		try {

			final Reader reader = new FileReader(filename);
			final PEMReader pemReader = new PEMReader(reader);

			Object object;
			while ((object = pemReader.readObject()) != null) {
				if (object instanceof KeyPair) {
					keyPair = (KeyPair) object;
				}
			}

			reader.close();
			return keyPair;

		} catch (FileNotFoundException e) {
			return null;
		}

	}

	/**
	 * Generates a new RSA KeyPair and saves the private key in PEM format to
	 * the specified filename
	 * 
	 * @param filename
	 *            The filename to write out a RSA private key in PEM format
	 * @return The generated RSA {@link KeyPair}
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static KeyPair generateKeyPairAndSaveToFile(String filename)
			throws NoSuchProviderException, NoSuchAlgorithmException,
			IOException {

		KeyPair keyPair = generateRSAKeyPair();

		final Writer writer = new FileWriter(filename);
		final PEMWriter pemWriter = new PEMWriter(writer);
		pemWriter.writeObject(keyPair.getPrivate());
		pemWriter.close();

		return keyPair;

	}

	public static void saveKeypairToFile(KeyPair keyPair, String filename)
			throws IOException {
		final Writer writer = new FileWriter(filename);
		final PEMWriter pemWriter = new PEMWriter(writer);
		pemWriter.writeObject(keyPair.getPrivate());
		pemWriter.close();
	}

	/**
	 * Generates a valid CA certificate and saves it in PEM format to the
	 * specified filename
	 * 
	 * @param filename
	 *            The filename to write out a CA certificate in PEM format
	 * @param keyPair
	 *            A private/public {@link KeyPair} to sign the CA certificate
	 *            with
	 * @return The generated certificate
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateEncodingException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws IOException
	 */
	public static X509Certificate generateCACertificateAndSaveToFile(
			String filename, KeyPair keyPair) throws NoSuchAlgorithmException,
			CertificateEncodingException, NoSuchProviderException,
			InvalidKeyException, SignatureException, IOException {

		X509Certificate certificate = generateV1Certificate(keyPair, "hj", 4);

		final Writer writer = new FileWriter(filename);
		final PEMWriter pemWriter = new PEMWriter(writer);
		pemWriter.writeObject(certificate);
		pemWriter.close();

		return certificate;

	}

	

	/**
	 * @param serialno
	 * @return
	 * @throws Exception
	 */
	public static Certificate[] exportCertificateChain(String serialno) throws Exception{
		ArrayList<X509Certificate> cert=new ArrayList<X509Certificate>();
		X509Certificate certificate = null;
		Object obj=null;
		do{
			obj=getCertificateBySerialNo(serialno);
			
			if(obj instanceof org.sumanta.bean.Certificate){
				certificate=(X509Certificate) SamCA.deserialize(new ByteArrayInputStream( ((org.sumanta.bean.Certificate) obj).getCertificate() ));
				serialno=((org.sumanta.bean.Certificate) obj).getIssuerserailno();
			}
			if(obj instanceof org.sumanta.bean.CA){
				certificate=(X509Certificate) SamCA.deserialize(new ByteArrayInputStream( ((org.sumanta.bean.CA) obj).getCertificate() ));
				serialno=((org.sumanta.bean.CA) obj).getIssuerserailno();
			}
			if(obj instanceof org.sumanta.bean.RootCA){
				certificate=(X509Certificate) SamCA.deserialize(new ByteArrayInputStream( ((org.sumanta.bean.RootCA) obj).getCertificate() ));
				System.out.println(((X509Certificate) certificate).getSubjectDN());
			}
			cert.add(certificate);
		}
		while(!certificate.getIssuerDN().equals( certificate.getSubjectDN()));

		Certificate[] ret=new Certificate[cert.size()];
		int i=0;
		for (Certificate certificate2 : cert) {
			ret[i]=certificate2;
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
	public static void exportCertificateToFile(Type type, String serialNo,
			String file,Category cat,Format format) {
		X509Certificate cert = null;
		KeyPair key=null;
		try {
			String query = "";
			if (serialNo != null && serialNo.equals("")) {
				query = query + "where serialno='" + serialNo + "'";
			}
			if (type.equals(Type.ca)) {
				CAManager cmngr = new CAManager();
				List l = cmngr.viewCAs(query);
				if (l.size() != 0) {
					Iterator it = l.listIterator();
					while (it.hasNext()) {
						CA object = (CA) it.next();
						cert = (X509Certificate) deserialize(new ByteArrayInputStream(
								object.getCertificate()));
						key = (KeyPair) deserialize(new ByteArrayInputStream(object.getKeypair()));
					}
				}
			}

			if (type.equals(Type.rootca)) {
				final RootCAManager rcmngr = new RootCAManager();
				List l = rcmngr.viewRootCAs(query);
				Iterator it = l.listIterator();
				while (it.hasNext()) {
					RootCA object = (RootCA) it.next();
					cert = (X509Certificate) deserialize(new ByteArrayInputStream(
							object.getCertificate()));
					key = (KeyPair) deserialize(new ByteArrayInputStream(object.getKeypair()));
				}
			}

			if (type.equals(Type.certificate)) {
				CertificateManager cmngr = new CertificateManager();
				List l = cmngr.viewCertificates(query);
				Iterator it = l.listIterator();
				while (it.hasNext()) {
					org.sumanta.bean.Certificate object = (org.sumanta.bean.Certificate) it
							.next();
					cert = (X509Certificate) deserialize(new ByteArrayInputStream(
							object.getCertificate()));
					key = (KeyPair) deserialize(new ByteArrayInputStream(object.getKeypair()));
				}
			}
			if(Category.keystore==cat){
				final Certificate[] certificate={cert};
				if(Format.p12==format){
					ToP12.toP12withPrivateKey("key", "secret",certificate , key.getPrivate(), "key.p12");
				}
				else {
					ToJKS.toJKSKeyStore(certificate,key.getPrivate(), "secret", "key", file);
				}
			}else if(Category.truststore==cat) {
				if(Format.jks==format){
					ToJKS.toJKSTrustStore(SamCA.exportCertificateChain(cert.getSerialNumber().toString()), "secret", file);
				}
				//ToP12.toP12withPrivateKey("key", "secret",
					//	     SamCA.exportCertificateChain(cert.getSerialNumber().toString()), key.getPrivate(), "key.p12");
			}else if(Category.certificate==cat) {
				saveCertificateToFile(cert, file);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public static void saveCertificateToFile(Certificate certificate,
			String name) throws NoSuchAlgorithmException,
			CertificateEncodingException, NoSuchProviderException,
			InvalidKeyException, SignatureException, IOException {
		final Writer writer = new FileWriter(name);
		final PEMWriter pemWriter = new PEMWriter(writer);
		pemWriter.writeObject(certificate);
		pemWriter.close();
	}

	public static Object getCertificateBySerialNo(String serialNo) {
		Object object = null;
		List l=null;
		try {
			String query = "";
			if (serialNo != null && !serialNo.equals("")) {
				query = query + "where serialno='" + serialNo + "'";
			}
			CertificateManager cmngr = new CertificateManager();
			l= cmngr.viewCertificates(query);
			
			if(l.isEmpty()){
				CAManager camngr=new CAManager();
				l=camngr.viewCAs(query);
			}
			if(l.isEmpty()){
				RootCAManager rcamngr=new RootCAManager();
				l=rcamngr.viewRootCAs(query);
			}
			
				if(l.get(0) instanceof org.sumanta.bean.Certificate){
					object=(org.sumanta.bean.Certificate)l.get(0);
				}else if (l.get(0) instanceof org.sumanta.bean.CA) {
					object=(org.sumanta.bean.CA)l.get(0);
				}else if (l.get(0) instanceof org.sumanta.bean.RootCA) {
					object=(org.sumanta.bean.RootCA)l.get(0);
				}
				
		
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(l!=null){
				l.clear();
			}
			return object;
		}
	}
	/**
	 * @param serialNo
	 * @return
	 */
	public static X509Certificate getCACertificateBySerialNo(String serialNo) {
		X509Certificate cert = null;
		try {
			String query = "";
			if (serialNo != null && serialNo.equals("")) {
				query = query + "where serialno='" + serialNo + "'";
			}

			CAManager cmngr = new CAManager();
			RootCAManager rcmngr = new RootCAManager();
			List l = cmngr.viewCAs(query);
			if (l.size() != 0) {
				Iterator it = l.listIterator();
				while (it.hasNext()) {
					CA object = (CA) it.next();
					cert = (X509Certificate) deserialize(new ByteArrayInputStream(
							object.getCertificate()));
				}
			} else {
				l = rcmngr.viewRootCAs(query);
				Iterator it = l.listIterator();
				while (it.hasNext()) {
					RootCA object = (RootCA) it.next();
					cert = (X509Certificate) deserialize(new ByteArrayInputStream(
							object.getCertificate()));
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
	public static KeyPair getCAKeyPairBySerialNo(String serialNo) {
		KeyPair keypair = null;
		try {
			String query = "";
			if (serialNo != null && serialNo.equals("")) {
				query = query + "where serialno='" + serialNo + "'";
			}

			CAManager cmngr = new CAManager();
			RootCAManager rcmngr = new RootCAManager();
			List l = cmngr.viewCAs(query);
			if (l.size() != 0) {
				final Iterator it = l.listIterator();
				while (it.hasNext()) {
					CA object = (CA) it.next();
					keypair = (KeyPair) deserialize(new ByteArrayInputStream(
							object.getKeypair()));
				}
			} else {
				l = rcmngr.viewRootCAs(query);
				Iterator it = l.listIterator();
				while (it.hasNext()) {
					RootCA object = (RootCA) it.next();
					keypair = (KeyPair) deserialize(new ByteArrayInputStream(
							object.getKeypair()));
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
	public static void createCA(String issuer, String commonName,
			long validityinDays, KeyPair keypair) {
		try {
			if (keypair == null) {
				keypair = SamCA.generateRSAKeyPair();
			}
			Certificate rootca = SamCA.getCACertificateBySerialNo(issuer);
			KeyPair rootkKeyPair = SamCA.getCAKeyPairBySerialNo(issuer);
			// PKCS10CertificationRequest cr = SamCA.generateCSR(keypair, "CN="
			// + commonName);

			SamCA ca = new SamCA((X509Certificate) rootca, rootkKeyPair);
			ca.issueCertificate(keypair, "CN=" + commonName,
					(int) validityinDays, KeyPurposeId.id_kp_ipsecUser);
			KeyPair kpair = ca.getIssuedKeyPair();
			X509Certificate issedcert = ca.getIssuedCertificate();
			CA myca = new CA();
			myca.setIssuer(issedcert.getIssuerDN().toString());
			myca.setSerialno(issedcert.getSerialNumber().toString());
			myca.setIssuerserailno(issuer);
			myca.setDn(issedcert.getSubjectDN().toString());
			myca.setCertificate(serialize(issedcert));
			myca.setKeypair(serialize(kpair));
			CAManager rcm = new CAManager();
			rcm.addCA(myca);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param commonName
	 * @param days
	 */
	public static void createRootCA(String commonName, double days) {
		try {
			final KeyPair keyPair = generateRSAKeyPair();
			X509Certificate certificate = generateV1Certificate(keyPair,
					commonName, days);

			/*
			 * final String str = "CertDB"; final Connection localConnection =
			 * DriverManager .getConnection(Constant.protocol + str); final
			 * PreparedStatement ps = localConnection
			 * .prepareStatement(DBQuery.insertRootCA); ps.setInt(1, 1);
			 * ps.setString(2, certificate.getSubjectDN().toString());
			 * ps.setString(3, certificate.getSerialNumber().toString());
			 * ps.setString(4, certificate.getIssuerDN().toString());
			 * 
			 * byte[] certificateasByte = serialize(certificate);
			 * ByteArrayInputStream bais = new ByteArrayInputStream(
			 * certificateasByte); ps.setBinaryStream(5, bais,
			 * certificateasByte.length);
			 * 
			 * byte[] keyasByte = serialize(keyPair); bais = new
			 * ByteArrayInputStream(keyasByte); ps.setBinaryStream(6, bais,
			 * keyasByte.length); ps.executeUpdate(); localConnection.commit();
			 */
			RootCA rootca = new RootCA();
			// rootca.setId(1);
			rootca.setIssuer(certificate.getIssuerDN().toString());
			rootca.setSerialno(certificate.getSerialNumber().toString());
			rootca.setDn(certificate.getSubjectDN().toString());
			rootca.setCertificate(serialize(certificate));
			rootca.setKeypair(serialize(keyPair));
			RootCAManager rcm = new RootCAManager();
			rcm.addRootCA(rootca);
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
	public static void listCertificate(Type type, String commonName,
			String serialNo) throws Exception {
		X509Certificate cert = null;

		// final Connection localConnection = DriverManager
		// .getConnection(Constant.protocol + str);
		String query = "";
		if (commonName != null && !commonName.isEmpty()) {
			query = query + "where dn='" + commonName + "'";
		}
		if (serialNo != null && !serialNo.isEmpty()) {
			if (query.contains("where")) {
				query = query + "and serialno='" + serialNo + "'";
			} else {
				query = query + "where serialno='" + serialNo + "'";
			}
		}

		if (type.equals(Type.ca)) {
			CAManager cm = new CAManager();
			List l = cm.viewCAs(query);
			for (Object object : l) {
				CA ca = (CA) object;
				X509Certificate c = (X509Certificate) deserialize(new ByteArrayInputStream(
						ca.getCertificate()));
				System.out.print(ca.getId());
				System.out.print("\t");
				System.out.print(ca.getDn());
				System.out.print("\t");
				System.out.print(c.getSerialNumber());
				System.out.print("\t");
				System.out.print(c.getIssuerDN());
				System.out.print("\t");
				System.out.print(c.getNotBefore());
				System.out.print("\t");
				System.out.println(c.getNotAfter());
			}
		}
		if (type.equals(Type.rootca)) {
			// query = "select * from RootCA ";
			RootCAManager rcm = new RootCAManager();
			final List l = rcm.viewRootCAs("");
			for (Object object : l) {
				RootCA rca = (RootCA) object;
				X509Certificate c = (X509Certificate) deserialize(new ByteArrayInputStream(
						rca.getCertificate()));
				System.out.print(rca.getId());
				System.out.print("\t");
				System.out.print(rca.getDn());
				System.out.print("\t");
				System.out.print(c.getSerialNumber());
				System.out.print("\t");
				System.out.print(c.getIssuerDN());
				System.out.print("\t");
				System.out.print(c.getNotBefore());
				System.out.print("\t");
				System.out.println(c.getNotAfter());
			}
		}
		if (type.equals(Type.certificate)) {
			CertificateManager cm = new CertificateManager();
			List l = cm.viewCertificates(query);
			for (Object object : l) {
				org.sumanta.bean.Certificate certificate = (org.sumanta.bean.Certificate) object;
				X509Certificate c = (X509Certificate) deserialize(new ByteArrayInputStream(
						certificate.getCertificate()));
				System.out.print(certificate.getId());
				System.out.print("\t");
				System.out.print(certificate.getDn());
				System.out.print("\t");
				System.out.print(c.getSerialNumber());
				System.out.print("\t");
				System.out.print(c.getIssuerDN());
				System.out.print("\t");
				System.out.print(c.getNotBefore());
				System.out.print("\t");
				System.out.println(c.getNotAfter());
			}
		}

	}

	/**
	 * @throws SQLException
	 */
	public static void listCA() throws SQLException {
		X509Certificate cert = null;
		final String str = "CertDB";
		final Connection localConnection = DriverManager
				.getConnection(Constant.protocol + str);

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
				out.print(cert.getNotAfter());
			} catch (Exception e) {

			}
		}
	}

	/**
	 * @param issuer
	 * @param commonName
	 * @param validityinDays
	 * @param keypair
	 */
	public static void createCertificate(String issuer, String commonName,
			long validityinDays, KeyPair keypair) {

		try {
			if (keypair == null) {
				keypair = SamCA.generateRSAKeyPair();
			}
			Certificate rootca = SamCA.getCACertificateBySerialNo(issuer);
			KeyPair rootkKeyPair = SamCA.getCAKeyPairBySerialNo(issuer);
			// PKCS10CertificationRequest cr = SamCA.generateCSR(keypair, "CN="
			// + commonName);

			final SamCA ca = new SamCA((X509Certificate) rootca, rootkKeyPair);
			ca.issueCertificate(keypair, "CN=" + commonName,
					(int) validityinDays, KeyPurposeId.anyExtendedKeyUsage);
			KeyPair kpair = ca.getIssuedKeyPair();
			X509Certificate issedcert = ca.getIssuedCertificate();
			org.sumanta.bean.Certificate mycert = new org.sumanta.bean.Certificate();
			mycert.setIssuer(issedcert.getIssuerDN().toString());
			mycert.setSerialno(issedcert.getSerialNumber().toString());
			mycert.setIssuerserailno(issuer);
			mycert.setDn(issedcert.getSubjectDN().toString());
			mycert.setCertificate(serialize(issedcert));
			mycert.setKeypair(serialize(kpair));
			CertificateManager cm = new CertificateManager();
			cm.addCertificate(mycert);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
