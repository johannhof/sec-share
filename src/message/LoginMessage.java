package message;

public class LoginMessage extends Message {

    private final String username;
    private final String password;

    public LoginMessage(final String username, final String password) {
        super(OpCode.LOGIN);
		
		this.username = username;
		this.password = password;
		
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}
}
