package message;

import java.io.Serializable;


public abstract class Message implements Serializable {

    private static final long serialVersionUID = -1772974882205399828L;
    private final OpCode opcode;

    public Message(final OpCode msgOp) {
        this.opcode = msgOp;
    }

    public OpCode GetOp() {
        return this.opcode;
    }

}
