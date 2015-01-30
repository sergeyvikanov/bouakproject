package util;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class BouakMessageEncoder implements Encoder.Text<BouakMessage>{

	@Override
	public void init(final EndpointConfig config) {
	}
 
	@Override
	public void destroy() {
	}
 
	@Override
	public String encode(final BouakMessage chatMessage) throws EncodeException {
		return Json.createObjectBuilder()
				.add("type", chatMessage.getType())
				.add("sender", chatMessage.getSender())
				.add("content", chatMessage.getContent()).build()
				.toString();
	}
}


