package Message;

import java.io.Serializable;


public abstract class Message implements Serializable {

	
	//private static final long serialVersionUID =;
	private OpCode opcode;
	
	
	public Message(OpCode msgOp) {
		this.opcode = msgOp;
	}
	
	public OpCode GetOp() {
		return this.opcode;
	}
	
}
