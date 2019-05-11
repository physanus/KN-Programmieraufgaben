package de.danielprinz.hskl.nk.rsa.crypto;

import de.danielprinz.hskl.nk.rsa.Main;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class KryptoManager {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        SECURE_RANDOM.setSeed(4299209391323882146L);
    }


    public static byte[] encrypt(Key key, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, key);
        return rsaCipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(Key key, byte[] msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(Cipher.DECRYPT_MODE , key);
        byte[] decrypted = rsaCipher.doFinal(msg);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Generates a fresh, random pair of private and public RSA key
     * @param keysize The size of the key which should be generated
     * @return The keypair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair getFreshKeyPair(int keysize) throws NoSuchAlgorithmException {
        if(keysize < 512) throw new IllegalArgumentException("Keysize must be greater than 512");
        if(keysize > 16384) throw new IllegalArgumentException("Keysize must be less than 16385");

        Main.LOGGER.info("Generating " + keysize + " bit long keypair, this could take a while...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keysize, SECURE_RANDOM);
        Main.LOGGER.info("Successfully generated the keypair.");
        return keyGen.generateKeyPair();
    }


    public static KeyPair getKeysFromString(String keyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] keyPublic = keyString.split("; ")[0].replaceAll("\\[", "").replaceAll("]", "").split(", ");
        String[] keyPrivate = keyString.split("; ")[1].replaceAll("\\[", "").replaceAll("]", "").split(", ");

        byte[] keyPublicBytes = new byte[keyPublic.length];
        for(int j = 0; j < keyPublic.length; j++) {
            keyPublicBytes[j] = Byte.valueOf(keyPublic[j]);
        }
        Main.LOGGER.info("Found public key bytes: " + Arrays.toString(keyPublicBytes));

        byte[] keyReceiverPrivateBytes = new byte[keyPrivate.length];
        for(int j = 0; j < keyPrivate.length; j++) {
            keyReceiverPrivateBytes[j] = Byte.valueOf(keyPrivate[j]);
        }
        Main.LOGGER.info("Found private key bytes: " + Arrays.toString(keyReceiverPrivateBytes));


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec x509EncodedKeySpecPublic = new X509EncodedKeySpec(keyPublicBytes);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpecPublic);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpecPrivate = new PKCS8EncodedKeySpec(keyReceiverPrivateBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpecPrivate);


        return new KeyPair(publicKey, privateKey);
    }


    public static BigInteger getP(PrivateKey privateKey) {
        return ((RSAPrivateKey) privateKey).getModulus();
    }
    public static BigInteger getP(PublicKey publicKey) {
        return ((RSAPublicKey) publicKey).getModulus();
    }

    public static BigInteger getQ(PrivateKey privateKey) {
        return ((RSAPrivateKey) privateKey).getPrivateExponent();
    }

    public static BigInteger getE(PublicKey publicKey) {
        return ((RSAPublicKey) publicKey).getPublicExponent();
    }


}
