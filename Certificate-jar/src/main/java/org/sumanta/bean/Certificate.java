package org.sumanta.bean;

import javax.persistence.*;

@Entity(name = "Certificate")
public class Certificate {

    @Id
    // @GeneratedValue
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    public void setId(final int id) {
        this.id = id;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(final String dn) {
        this.dn = dn;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(final String serialno) {
        this.serialno = serialno;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }

    public String getIssuerserailno() {
        return issuerserialno;
    }

    public void setIssuerserailno(final String issuerserailno) {
        this.issuerserialno = issuerserailno;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(final byte[] certificate) {
        this.certificate = certificate;
    }

    public byte[] getKeypair() {
        return keypair;
    }

    public void setKeypair(final byte[] keypair) {
        this.keypair = keypair;
    }

}
