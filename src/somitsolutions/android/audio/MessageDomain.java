package somitsolutions.android.audio;

public class MessageDomain {

	private String Message_Name;
	private String Message_Message;

	public MessageDomain(){}

	public MessageDomain(String name, String message){
		this.Message_Name = name;
		this.Message_Message = message;
	}
	
	public String getMessage_Name() {
		return Message_Name;
	}

    public void setMessage_Name(String name) {
		this.Message_Name = name;
	}

    public String getMessage_Message() {
		return Message_Message;
	}

    public void setMessage_Message(String message) {
		this.Message_Message = message;
	}
    @Override
	public String toString() {
		return Message_Name;
	}
}
