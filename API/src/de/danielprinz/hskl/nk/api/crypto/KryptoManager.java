package de.danielprinz.hskl.nk.api.crypto;

import javax.crypto.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;

public class KryptoManager {

    // Define a secure random for better randomized keys
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        SECURE_RANDOM.setSeed(4299209391323882146L);
    }


    /**
     * Encrypts a given String using the provided key, either private (for authentication) or public (for encryption)
     * @param key The private or public key
     * @param msg The String to be encrypted
     * @return The encrypted byte-Array
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     */
    public static String encrypt(Key key, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, key);
        return encodeHex(rsaCipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Decrypts a given String using the provided key, either private (for encryption) or public (for authentication)
     * @param key The private or public key
     * @param msg The String to be decrypted
     * @return The decrypted String, UTF-8 encoded
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decrypt(Key key, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/NoPadding");
        rsaCipher.init(Cipher.DECRYPT_MODE , key);
        byte[] decrypted = rsaCipher.doFinal(decodeHex(msg));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Generates a fresh, random pair of private and public RSA keys
     * @param keysize The size of the key which should be generated, range: 512-16385
     * @return The generated keypair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair getFreshKeyPair(int keysize) throws NoSuchAlgorithmException {
        if(keysize < 512) throw new IllegalArgumentException("Keysize must be greater than 512");
        if(keysize > 16384) throw new IllegalArgumentException("Keysize must be less than 16385");

        LoggerUtil.getInstance().log(Level.INFO, "Generating " + keysize + " bit long keypair, this could take a while...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keysize, SECURE_RANDOM);
        LoggerUtil.getInstance().log(Level.INFO, "Successfully generated the keypair.");
        return keyGen.generateKeyPair();
    }

    /**
     * Generates a fresh, random key
     * @return The generated keypair
     * @throws NoSuchAlgorithmException
     */
    public static SecretKey getFreshDESKey() throws NoSuchAlgorithmException {
        LoggerUtil.getInstance().log(Level.INFO, "Generating " + 56 + " bit long key, this could take a while...");
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56, SECURE_RANDOM);
        LoggerUtil.getInstance().log(Level.INFO, "Successfully generated the keypair.");
        return keyGen.generateKey();
    }

    /**
     * Retrieves the keys from the resective String-representation
     * @param keyString The String to be retrieved
     * @return The KeyPair [private & public key included]
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static KeyPair getKeysFromString(String keyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyPublic = decodeHex(keyString.split(", ")[0]);
        byte[] keyPrivate = decodeHex(keyString.split(", ")[1]);

        LoggerUtil.getInstance().log(Level.INFO,"Found public key bytes: " + Arrays.toString(keyPublic));
        LoggerUtil.getInstance().log(Level.INFO,"Found private key bytes: " + Arrays.toString(keyPrivate));

        // convert bytes to keys
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec x509EncodedKeySpecPublic = new X509EncodedKeySpec(keyPublic);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpecPublic);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpecPrivate = new PKCS8EncodedKeySpec(keyPrivate);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpecPrivate);

        return new KeyPair(publicKey, privateKey);
    }

    /**
     * Calculates the md5 hash of a given String
     * @param s The string to be hashed
     * @return The hash
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(String s) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        return encodeHex(messageDigest.digest(s.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Encodes a given byte array to a String
     * @param bytes The byte array
     * @return The String
     */
    public static String encodeHex(byte[] bytes) {
        return new BigInteger(1, bytes).toString(16);
    }

    /**
     * Decodes a given Hex-String to a byte array
     * @param hex The Hex-String
     * @return the byte array
     */
    public static byte[] decodeHex(String hex) {
        byte[] bytes = new BigInteger(hex, 16).toByteArray();
        if(bytes[0] == 0)
            return Arrays.copyOfRange(bytes, 1, bytes.length);
        return bytes;
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
