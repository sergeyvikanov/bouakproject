package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

/**
 * @author BOUAK
 */
@ClientEndpoint
public class MyClient {
    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("Connected to endpoint: " + session.getBasicRemote());
        JsonObject jObject = Json.createObjectBuilder().build();

		jObject = Json
				.createObjectBuilder()
				.add("sender", "##bouakbot##")
				.add("content", "go").build();
		System.out.println("Sending message to endpoint: " + jObject.toString());
		session.getBasicRemote().sendText(jObject.toString());
    }
    
    @OnMessage
    public void processMessage(String message) {
        System.out.println("Received message in client: " + message);
    }
    
    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }
}
