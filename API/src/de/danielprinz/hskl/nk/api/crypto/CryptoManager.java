package de.danielprinz.hskl.nk.api.crypto;

import de.danielprinz.hskl.nk.api.crypto.pgp.PGPMessageHash;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;

public class CryptoManager {

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
    public static String encryptRSA(Key key, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return encodeHex(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
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
    public static String decryptRSA(Key key, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE , key);
        byte[] decrypted = cipher.doFinal(decodeHex(msg));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Decrypts a given String using the provided key, either private (for encryption) or public (for authentication). Returns the last `cutBytes` chars of the calculated String (e.g. for MD5)
     * @param key The private or public key
     * @param msg The String to be decrypted
     * @param amountOfLastChars The amount of chars to be cut at the end of the decrypted string
     * @return The decrypted String, UTF-8 encoded and cut
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decryptRSA(Key key, String msg, int amountOfLastChars) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        String decrypted = decryptRSA(key, msg);
        if(decrypted.length() - amountOfLastChars > 0) {
            return decrypted.substring(decrypted.length() - amountOfLastChars);
        } else {
            return decrypted;
        }
    }

    /**
     * Encrypts a given String using the provided key
     * @param secretKeySpec The AES key
     * @param msg The String to be encrypted
     * @return The encrypted byte-Array
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String encryptAES(SecretKeySpec secretKeySpec, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        return encodeHex(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Decrypts a given String using the provided AES key
     * @param secretKeySpec The AES key
     * @param msg The String to be decrypted
     * @return The decrypted String, UTF-8 encoded
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decryptAES(SecretKeySpec secretKeySpec, String msg) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(decodeHex(msg));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Generates a fresh, random pair of private and public RSA keys
     * @param keySize The size of the key which should be generated, range: 512-16385
     * @return The generated keypair
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair getFreshKeyPair(int keySize) throws NoSuchAlgorithmException {
        if(keySize < 512) throw new IllegalArgumentException("The key size must be greater than 512");
        if(keySize > 16384) throw new IllegalArgumentException("The key size must be less than 16385");

        LoggerUtil.log(Level.INFO, "Generating " + keySize + " bit long keypair, this could take a while...");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize, SECURE_RANDOM);
        LoggerUtil.log(Level.INFO, "Successfully generated the keypair.");
        return keyGen.generateKeyPair();
    }

    /**
     * Generates a fresh, random key
     * @return The generated key
     * @throws NoSuchAlgorithmException
     */
    public static String getFreshDESKey() throws NoSuchAlgorithmException {
        LoggerUtil.log(Level.INFO, "Generating " + 56 + " bit long key, this could take a while...");
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56, SECURE_RANDOM);
        LoggerUtil.log(Level.INFO, "Successfully generated the keypair.");
        return encodeHex(keyGen.generateKey().getEncoded());
    }

    /**
     * Generates a fresh, random AES key
     * @param key The key to use
     * @return The generated AES key
     * @throws NoSuchAlgorithmException
     */
    public static SecretKeySpec getAESKey(String key) throws NoSuchAlgorithmException {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(keyBytes), 16);
        LoggerUtil.log(Level.INFO, "Calculated AES key from String [" + key + "]: " + Arrays.toString(keyBytes));
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Retrieves the keys from the respective String-representation
     * @param keyString The String to be retrieved
     * @return The KeyPair [private & public key included]
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static KeyPair getKeysFromString(String keyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyPublic = decodeHex(keyString.split(", ")[0]);
        byte[] keyPrivate = decodeHex(keyString.split(", ")[1]);

        LoggerUtil.log(Level.INFO,"Found public key bytes: " + Arrays.toString(keyPublic));
        LoggerUtil.log(Level.INFO,"Found private key bytes: " + Arrays.toString(keyPrivate));

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
        String md5 = encodeHex(messageDigest.digest(s.getBytes(StandardCharsets.UTF_8)));
        LoggerUtil.log(Level.INFO, "Calculated MD5 from String [" + s + "]: " + md5);
        return md5;
    }

    /**
     * Calculates the MD5/RSA signature of a given string for the provided privateKey
     * @param privateKey The private key
     * @param s The string
     * @return The signature
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String getSignature(PrivateKey privateKey, String s) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        // Signature signature = Signature.getInstance("MD5withRSA");
        // signature.initSign(privateKey);
        // signature.update(s.getBytes(StandardCharsets.UTF_8));
        // return encodeHex(signature.sign());

        return new PGPMessageHash(s, privateKey).getMd5Encrypted();
    }

    public static boolean verifySignature(PublicKey publicKey, String s, String sign) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        // Signature signature = Signature.getInstance("MD5withRSA");
        // signature.initVerify(publicKey);
        // signature.update(s.getBytes(StandardCharsets.UTF_8));
        // return signature.verify(decodeHex(sign));

        String md5Expected = CryptoManager.getMD5(s);
        String md5Decrypted = CryptoManager.decryptRSA(publicKey, sign, md5Expected.length());

         System.out.println("md5Expected: " + md5Expected);
         System.out.println("md5Expected.length(): " + md5Expected.length());
         System.out.println("md5Decrypted: " + md5Decrypted);
         System.out.println("md5Decrypted.length(): " + md5Decrypted.length());

        if(md5Decrypted.equals(md5Expected)) {
            LoggerUtil.log(Level.FINE, "Signature was verified");
            return true;
        } else {
            LoggerUtil.log(Level.FINE, "Signature was not verified");
            return false;
        }
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


}
