package de.danielprinz.hskl.nk.publicKeyInfrastructure;

import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;
import de.danielprinz.hskl.nk.api.crypto.ca.CA;
import de.danielprinz.hskl.nk.api.crypto.ca.Cert;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {

        try {

            // GENERATE CERTIFICATES
            LoggerUtil.log(Level.FINE, "Generating the certificates now");

            // layer root
            CA root = new CA("root");
            root.generateCertificate(root);

            // layer 1
            CA ca1  = new CA("ca1");
            root.generateCertificate(ca1);

            CA ca2  = new CA("ca2");
            root.generateCertificate(ca2);

            // layer 2
            CA member1 = new CA("member1");
            ca1.generateCertificate(member1);

            CA member2 = new CA("member2");
            ca1.generateCertificate(member2);
            // manipulate `member2` cert (==> it will not be genuine anymore)
            try {
                Field f = Cert.class.getDeclaredField("issuer");
                f.setAccessible(true);
                f.set(member2.getCert(), "Fake Issuer");
                LoggerUtil.log(Level.FINE, "Manipulated certificate: " + member2.getCert().toString());
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }

            CA member3 = new CA("member3");
            ca2.generateCertificate(member3);



            // VERIFY THE CERTIFICATES
            LoggerUtil.log(Level.FINE, "");
            LoggerUtil.log(Level.FINE, "Validating the certificates now");

            // is valid
            if(member1.verifyCert()) {
                LoggerUtil.log(Level.FINE, "Signature of member1 is valid");
            } else {
                LoggerUtil.log(Level.FINE, "Signature of member1 is invalid");
            }

            // is invalid
            if(member2.verifyCert()) {
                LoggerUtil.log(Level.FINE, "Signature of member2 is valid");
            } else {
                LoggerUtil.log(Level.FINE, "Signature of member2 is invalid");
            }

            // is valid
            if(member3.verifyCert()) {
                LoggerUtil.log(Level.FINE, "Signature of member3 is valid");
            } else {
                LoggerUtil.log(Level.FINE, "Signature of member3 is invalid");
            }


        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

    }

}
