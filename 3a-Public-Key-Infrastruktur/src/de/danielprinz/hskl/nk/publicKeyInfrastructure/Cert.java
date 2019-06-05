package de.danielprinz.hskl.nk.publicKeyInfrastructure;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;

import java.security.*;

public class Cert {

    private String subject;
    private String publicKeyAlgorithm;
    private PublicKey publicKey;
    private String issuer;
    private String signatureAlgorithm;
    private String signature;

    public Cert(String subject, String publicKeyAlgorithm, KeyPair keyPair, String issuer, String signatureAlgorithm) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.subject = subject;
        this.publicKeyAlgorithm = publicKeyAlgorithm;
        this.publicKey = keyPair.getPublic();
        this.issuer = issuer;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = KryptoManager.getSignature(keyPair.getPrivate(), this.getString());
    }


    public String getSubject() {
        return subject;
    }

    public String getPublicKeyAlgorithm() {
        return publicKeyAlgorithm;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public String getSignature() {
        return signature;
    }



    public String getString() {
        return "Cert{" +
                "subject='" + subject + '\'' +
                ", publicKeyAlgorithm='" + publicKeyAlgorithm + '\'' +
                ", publicKey=" + KryptoManager.encodeHex(publicKey.getEncoded()) +
                ", issuer='" + issuer + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "Cert{" +
                "subject='" + subject + '\'' +
                ", publicKeyAlgorithm='" + publicKeyAlgorithm + '\'' +
                ", publicKey=" + KryptoManager.encodeHex(publicKey.getEncoded()) +
                ", issuer='" + issuer + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", signature=" + signature +
                '}';
    }

}
