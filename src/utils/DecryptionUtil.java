package utils;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * Class for creating of keys and decrypting.
 */
public class DecryptionUtil {
    /**
     * Algorithm of encryption.
     */
    public static final String ALGO = "RSA";
    /**
     * Name of directory in which files with keys will be sotred.
     */
    public static final String PATHNAME = System.getProperty("user.home") + "/server_impl/";
    /**
     * Name of file in which private key will be stored.
     */
    public static final String PRIVATE_KEY_FILE = PATHNAME + "private.key";
    /**
     * Name of file in which public key will be stored.
     */
    public static final String PUBLIC_KEY_FILE = PATHNAME + "public.key";
    /**
     * Byte array with public key.
     */
    private static byte[] public_key_bytes;
    /**
     * Private key.
     */
    private static PrivateKey privateKey;
    /**
     * Decrypts password using {@link #privateKey}.
     * @param pass encrypted password.
     * @return decrypted password.
     */
    public static String decrypt(String pass) {

        byte[] dectyptedText = null;
        byte[] p = Base64.getDecoder().decode(pass);
        try {
            final Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            dectyptedText = cipher.doFinal(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            return new String(dectyptedText,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Initializes {@link #privateKey} and {@link #public_key_bytes}.
     * If keys don't exist, invokes {@link #generateKey()} to generate key pair.
     */
    public static void configKeys()
    {
        if (!areKeysPresent()) {
            generateKey();
        }
            try {
                File filePublicKey = new File(PUBLIC_KEY_FILE);
                FileInputStream fis = new FileInputStream(PUBLIC_KEY_FILE);
                public_key_bytes = new byte[(int) filePublicKey.length()];
                fis.read(public_key_bytes);
                fis.close();

                File filePrivateKey = new File(PRIVATE_KEY_FILE);
                fis = new FileInputStream(PRIVATE_KEY_FILE);
                byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
                fis.read(encodedPrivateKey);
                fis.close();

                KeyFactory keyFactory = KeyFactory.getInstance(ALGO);
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
                privateKey = keyFactory.generatePrivate(privateKeySpec);

            } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                //e.printStackTrace();
            }


    }
     /**
     * Gets string with public key.
     * @return string with public key.
     */
    public static String getPublicKey()
    {
        return Base64.getEncoder().encodeToString(public_key_bytes);
    }
    /**
     * Checks if keys already exist.
     * @return true if exist, else - false.
     */
    private static boolean areKeysPresent() {

        File privateKey = new File(PRIVATE_KEY_FILE);
        File publicKey = new File(PUBLIC_KEY_FILE);

        return privateKey.exists() && publicKey.exists();
    }
    /**
     * Generates key pair which contains a private and public key.
     * Writes it into files.
     */
    private static void generateKey() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGO);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File privateKeyFile = new File(PRIVATE_KEY_FILE);
            File publicKeyFile = new File(PUBLIC_KEY_FILE);

            privateKeyFile.createNewFile();
            publicKeyFile.createNewFile();

            PublicKey publicKey = key.getPublic();
            PrivateKey privateKey = key.getPrivate();

            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            FileOutputStream fos = new FileOutputStream(PUBLIC_KEY_FILE);
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();

            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            fos = new FileOutputStream(PRIVATE_KEY_FILE);
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}