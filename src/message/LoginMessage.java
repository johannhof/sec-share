package message;

public class LoginMessage extends Message {

	private String username;
	private String password;
	
	public LoginMessage(String username, String password) {
		super(OpCode.LOGIN);
		
		this.username = username;
		this.password = password;
		
	}

}
