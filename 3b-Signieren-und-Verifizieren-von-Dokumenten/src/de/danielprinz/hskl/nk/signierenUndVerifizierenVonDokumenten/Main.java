package de.danielprinz.hskl.nk.signierenUndVerifizierenVonDokumenten;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;
import de.danielprinz.hskl.nk.api.crypto.ca.CA;
import de.danielprinz.hskl.nk.api.crypto.ca.Cert;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String document = "This is a secret document";
        ArrayList<CA> cas = new ArrayList<>();

        try {

            CA root = new CA("root");
            cas.add(root);
            root.generateCertificate(root);
            CA alice = new CA("alice");
            cas.add(alice);
            root.generateCertificate(alice);


            Cert aliceCert = alice.getCert();
            String signature = alice.getSignature(document);
            System.out.println("signature: " + signature);


            // Überprüfen des Dokuments (Authentizität & Integrität)
            System.out.println(KryptoManager.verifySignature(aliceCert.getPublicKey(), document, signature));

            // Überprüfen des öffentlichen Schlüssels
            System.out.println(aliceCert.verifyCert(cas));





        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

}
