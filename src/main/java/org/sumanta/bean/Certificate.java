package org.sumanta.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "Certificate")
public class Certificate {

	@Id
	// @GeneratedValue
	@GeneratedValue
	@Column(name = "ID")
	int id;
	@Column(name = "commonname", nullable = false)
	String dn;
	@Column(name = "serialno", nullable = false)
	String serialno;
	@Column(name = "issuer", nullable = false)
	String issuer;
	@Column(name = "issuerserialno", nullable = false)
	String issuerserialno;
	@Column(name = "certificate", nullable = false, length = 65000)
	byte[] certificate;
	@Column(name = "keypair", nullable = false, length = 65000)
	byte[] keypair;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getIssuerserailno() {
		return issuerserialno;
	}

	public void setIssuerserailno(String issuerserailno) {
		this.issuerserialno = issuerserailno;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public byte[] getKeypair() {
		return keypair;
	}

	public void setKeypair(byte[] keypair) {
		this.keypair = keypair;
	}

}
