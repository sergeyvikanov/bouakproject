package websocket.chat;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator
{
	private static ConcurrentHashMap<String, BouakJeu> listeServeurs = new ConcurrentHashMap<String, BouakJeu>();
	private HttpSession ses;
    @Override
    public void modifyHandshake(ServerEndpointConfig config, 
                                HandshakeRequest request, 
                                HandshakeResponse response)
    {
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        ses = httpSession;
        config.getUserProperties().put(HttpSession.class.getName(),httpSession);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public BouakJeu getEndpointInstance(Class BouakJeu) throws InstantiationException, NullPointerException {
    	String partie = ses.getAttribute("partie").toString();
    	if (listeServeurs.containsKey(partie))
    	{
    		return listeServeurs.get(partie);
    	}
    	else
    	{
    		listeServeurs.put(partie, new BouakJeu());
    		return listeServeurs.get(partie);
    	}
    }
    
    
}
