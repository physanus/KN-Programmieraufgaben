package de.danielprinz.hskl.nk.pgp;

public class PGPMessage {

    static final String SPLIT_STRING = ";.,;";

    private String message;
    private String md5Encrypted;
    private String keyEncrypted;

    public PGPMessage(String message, String md5Encrypted, String keySymmetricEncrypted) {
        if(message != null && message.equals("null")) message = null;
        if(md5Encrypted != null && md5Encrypted.equals("null")) md5Encrypted = null;
        if(keySymmetricEncrypted != null && keySymmetricEncrypted.equals("null")) keySymmetricEncrypted = null;

        this.message = message;
        this.md5Encrypted = md5Encrypted;
        this.keyEncrypted = keySymmetricEncrypted;
    }


    public String getMd5Encrypted() {
        return md5Encrypted;
    }

    public String getKeyEncrypted() {
        return keyEncrypted;
    }

    public String getMessage() {
        return message;
    }

    public String getString() {
        return message + SPLIT_STRING + md5Encrypted + SPLIT_STRING + keyEncrypted;
    }

    public boolean isAuthentication() {
        return md5Encrypted != null && !md5Encrypted.isEmpty();
    }

    public boolean isConfidentiality() {
        return keyEncrypted != null && !keyEncrypted.isEmpty();
    }

    public static PGPMessage getPGPMessage(String pgpMessage) {
        String[] pgpMessageSplit = pgpMessage.split(SPLIT_STRING);
        return new PGPMessage(pgpMessageSplit[0], pgpMessageSplit[1], pgpMessageSplit[2]);
    }


    @Override
    public String toString() {
        return "PGPMessage{" +
                "message='" + message + '\'' +
                ", md5Encrypted='" + md5Encrypted + '\'' +
                ", keyEncrypted='" + keyEncrypted + '\'' +
                ", getString()='" + getString() + '\'' +
                '}';
    }
}
