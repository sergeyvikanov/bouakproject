package util;

import java.io.StringReader;
 
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
 
public class BouakMessageDecoder implements Decoder.Text<BouakMessage> {
	@Override
	public void init(final EndpointConfig config) {
	}
 
	@Override
	public void destroy() {
	}
 
	@Override
	public BouakMessage decode(final String textMessage) throws DecodeException {
		BouakMessage BouakMessage = new BouakMessage();
		JsonObject obj = Json.createReader(new StringReader(textMessage))
				.readObject();
		BouakMessage.setType(obj.getString("type"));
		BouakMessage.setSender(obj.getString("sender"));
		BouakMessage.setContent(obj.getString("content"));
		
		return BouakMessage;
	}
 
	@Override
	public boolean willDecode(final String s) {
		return true;
	}
}
	