package de.danielprinz.hskl.nk.api.crypto.ca;

import de.danielprinz.hskl.nk.api.crypto.CryptoManager;
import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.logging.Level;

public class CA {

    private String name;
    private KeyPair keyPair;
    private Cert cert;
    private CA rootCA;

    public CA(String name) throws NoSuchAlgorithmException {
        this.name = name;
        this.keyPair = CryptoManager.getFreshKeyPair(1024);
    }


    /**
     * Generates the Cert for the provided CA
     * @param ca The CA
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public void generateCertificate(CA ca) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        ca.rootCA = this;
        ca.cert = new Cert(ca.name, "RSA/ECB/NoPadding", keyPair.getPrivate(), ca.keyPair.getPublic(), name, "MD5withRSA");
        LoggerUtil.log(Level.INFO, "Generated certificate: " + ca.cert);
    }


    public String getName() {
        return name;
    }

    public Cert getCert() {
        return cert;
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public boolean verifyCert() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        if(this == rootCA) return true;
        return CryptoManager.verifySignature(rootCA.getPublicKey(), cert.getString(), cert.getSignature()) && rootCA.verifyCert();
    }

    public String getSignature(String s) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        return CryptoManager.getSignature(keyPair.getPrivate(), s);
    }



}
