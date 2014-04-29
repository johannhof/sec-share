package external;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

public class CertificationAuthority {
    private static final CertificationAuthority ourInstance = new CertificationAuthority();
    private PrivateKey key;
    private Certificate certificate;
    private Signature verifySignature;
    private Signature signSignature;
    private String path;

    public static CertificationAuthority getInstance() {
        return ourInstance;
    }

    public void init(final String path) {
        // get user password and file input stream
        final String password = "authority";

        try (FileInputStream fis = new FileInputStream(path)) {
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, password.toCharArray());
            certificate = ks.getCertificate("authority");
            final KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) ks.getEntry("authority", new KeyStore.PasswordProtection(password.toCharArray()));
            key = entry.getPrivateKey();
            signSignature = Signature.getInstance("SHA256withRSA");
            signSignature.initSign(key);
            verifySignature = Signature.getInstance("SHA256withRSA");
            verifySignature.initVerify(certificate);
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | UnrecoverableEntryException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private CertificationAuthority() {
    }

    public byte[] sign(final Certificate certificate) throws SignatureException, CertificateEncodingException, InvalidKeyException {
        final byte[] encoded = certificate.getEncoded();
        signSignature.sign(encoded, 0, encoded.length);
        return signSignature.sign();
    }

    public boolean verify(final byte[] sig) throws SignatureException {
        return verifySignature.verify(sig);
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
