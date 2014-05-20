package somitsolutions.android.audio;

public class MessageDomain {

	private String Message_Url;
	private String Message_Time;

	public MessageDomain(){}

	public MessageDomain(String url, String time){
		this.Message_Url = url;
		this.Message_Time = time;
	}
	
	public String getMessage_Url() {
		return Message_Url;
	}

    public void setMessage_Url(String Url) {
		this.Message_Url = Url;
	}

    public String getMessage_Time() {
		return Message_Time;
	}

    public void setMessage_Time(String time) {
		this.Message_Time = time;
	}
    @Override
	public String toString() {
		return Message_Url;
	}
}
