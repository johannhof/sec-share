package message;

import java.security.cert.Certificate;

public class LoginMessage extends Message {

    private final String username;
    private final String password;
    private final Certificate certificate;

    public LoginMessage(final String username, final String password, final Certificate certificate) {
        super(OpCode.LOGIN);

        this.username = username;
        this.password = password;

        this.certificate = certificate;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
