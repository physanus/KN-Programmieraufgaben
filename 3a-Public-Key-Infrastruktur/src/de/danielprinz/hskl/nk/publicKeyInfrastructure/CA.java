package de.danielprinz.hskl.nk.publicKeyInfrastructure;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;
import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.logging.Level;

public class CA {

    private String name;
    private KeyPair keyPair;
    private Cert cert;
    private CA rootCA;

    public CA(String name) throws NoSuchAlgorithmException {
        this.name = name;
        this.keyPair = KryptoManager.getFreshKeyPair(1024);
    }


    /**
     * Generates the CA for the provided issuer
     * @param issuer
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public void generateCertificate(CA issuer) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        this.rootCA = issuer;
        this.cert = new Cert(name, "RSA/ECB/NoPadding", keyPair, issuer.getName(), "MD5withRSA");
        LoggerUtil.log(Level.INFO, "Generated certificate: " + cert);
    }


    public String getName() {
        return name;
    }

    public Cert getCert() {
        return cert;
    }


    public boolean verifyCert() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if(this == rootCA) return true;
        return KryptoManager.verifySignature(cert.getPublicKey(), cert.getString(), cert.getSignature()) && rootCA.verifyCert();
    }



}
