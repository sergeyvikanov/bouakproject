package util;

public class BouakMessage
{
	private String type;
	private String sender;
	private String content;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public BouakMessage(String type, String sender, String content) {
		this.type = type;
		this.sender = sender;
		this.content = content;
	}
	public BouakMessage() {
		this.type = null;
		this.sender = null;
		this.content = null;
	}
	

}
