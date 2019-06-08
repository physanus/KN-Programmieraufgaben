package de.danielprinz.hskl.nk.api.crypto.ca;

import de.danielprinz.hskl.nk.api.crypto.CryptoManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.ArrayList;

public class Cert {

    private String subject;
    private String publicKeyAlgorithm;
    private PublicKey publicKey;
    private String issuer;
    private String signatureAlgorithm;
    private String signature;

    public Cert(String subject, String publicKeyAlgorithm, KeyPair keyPair, String issuer, String signatureAlgorithm) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        this.subject = subject;
        this.publicKeyAlgorithm = publicKeyAlgorithm;
        this.publicKey = keyPair.getPublic();
        this.issuer = issuer;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signature = CryptoManager.getSignature(keyPair.getPrivate(), this.getString());
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


    /**
     * Verifies the cert
     * @param cas A list of available CAs
     * @return A boolean. True: verified. False: not verified.
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public boolean verifyCert(ArrayList<CA> cas) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        for(CA ca : cas) {
            if(ca.getCert() == this) {
                return ca.verifyCert();
            }
        }
        return false;
    }



    public String getString() {
        return "Cert{" +
                "subject='" + subject + '\'' +
                ", publicKeyAlgorithm='" + publicKeyAlgorithm + '\'' +
                ", publicKey=" + CryptoManager.encodeHex(publicKey.getEncoded()) +
                ", issuer='" + issuer + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                '}';
    }

    @Override
    public String toString() {
        return "Cert{" +
                "subject='" + subject + '\'' +
                ", publicKeyAlgorithm='" + publicKeyAlgorithm + '\'' +
                ", publicKey=" + CryptoManager.encodeHex(publicKey.getEncoded()) +
                ", issuer='" + issuer + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", signature=" + signature +
                '}';
    }

}
