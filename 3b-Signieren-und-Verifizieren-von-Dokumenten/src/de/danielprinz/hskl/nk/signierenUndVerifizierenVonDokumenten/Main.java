package de.danielprinz.hskl.nk.signierenUndVerifizierenVonDokumenten;

import de.danielprinz.hskl.nk.api.crypto.CryptoManager;
import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;
import de.danielprinz.hskl.nk.api.crypto.ca.CA;
import de.danielprinz.hskl.nk.api.crypto.ca.Cert;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {

        String document = "This is a secret document";
        ArrayList<CA> cas = new ArrayList<>();

        try {

            // GENERATE CERTIFICATES
            LoggerUtil.log(Level.FINE, "Generating the certificates now");

            CA root = new CA("root");
            cas.add(root);
            root.generateCertificate(root);
            CA alice = new CA("alice");
            cas.add(alice);
            root.generateCertificate(alice);

            // get Certificate from CA
            Cert aliceCert = alice.getCert();

            // generate signature for document
            String signature = alice.getSignature(document);
            LoggerUtil.log(Level.FINE, "Document: " + document);
            LoggerUtil.log(Level.FINE, "Signature of the document: " + signature);
            

            // Validation of the document (authenticity & integrity)
            if(CryptoManager.verifySignature(aliceCert.getPublicKey(), document, signature)) {
                LoggerUtil.log(Level.FINE, "The signature could be verified");
            } else {
                LoggerUtil.log(Level.FINE, "The signature could not be verified");
            }

            // Validation of the public key
            if(aliceCert.verifyCert(cas)) {
                LoggerUtil.log(Level.FINE, "The certificate could be verified");
            } else {
                LoggerUtil.log(Level.FINE, "The certificate could not be verified");
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

}
