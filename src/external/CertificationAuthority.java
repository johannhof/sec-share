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

    public static CertificationAuthority getInstance() {
        return ourInstance;
    }

    private CertificationAuthority() {

        // get user password and file input stream
        final String password = "authority";

        try (FileInputStream fis = new FileInputStream("authority.jks")) {
            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(fis, password.toCharArray());
            certificate = ks.getCertificate("authority");
            final KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) ks.getEntry("authority", new KeyStore.PasswordProtection(password.toCharArray()));
            key = entry.getPrivateKey();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }
    }

    public byte[] sign(final Certificate certificate) throws SignatureException, NoSuchAlgorithmException, CertificateEncodingException, InvalidKeyException {
        final Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(key);
        final byte[] encoded = certificate.getEncoded();
        s.sign(encoded, 0, encoded.length);
        return s.sign();
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
