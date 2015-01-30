/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package websocket.chat;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.json.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ImmutableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;

import util.Bordel;
import util.DamerauLevenshtein;
import util.HTMLFilter;

@ServerEndpoint(value = Bordel.adresse, configurator = GetHttpSessionConfigurator.class)
public class BouakJeu implements Bordel
{

	private static final Log log = LogFactory.getLog(BouakJeu.class);

	private static final String GUEST_PREFIX = "Joueur";
	private static final AtomicInteger connectionIds = new AtomicInteger(0);
	private static final Set<BouakJeu> connections = new CopyOnWriteArraySet<>();
	private static HashMap<Integer,String> JoueursConnectes;
	public  String nickname;
	public Session session;
	private HttpSession httpSession;
	private String numPartie = null;
	private int duree;
	private static boolean questionFinie = false;
	private Date date;
	private String question = "";
	private int ind = 0;
	private int qnum=1;
	private Date datelimite;
	private Qreponse questionCourante = new Qreponse();
	private String[] tableau;
	private ConcurrentHashMap<String,Qreponse> questions = new ConcurrentHashMap<String,Qreponse>();
	public BouakJeu()
	{
		nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
	}

	@OnOpen
	public void start(final Session session, @PathParam("numero") String toto, EndpointConfig config)
			throws Exception
	{
		this.session = session;
		this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		this.nickname = httpSession.getAttribute("login").toString();
		if (httpSession.getAttribute("partie").toString() != null)
		{
			numPartie = httpSession.getAttribute("partie").toString();
			session.getUserProperties().put("partie", numPartie);
		}
		System.out.println("bouakjeu:"+this.toString());
		System.out.println("session:"+session.toString());
		System.out.println("sessionID:"+session.getId());
		System.out.println("openSessions:"+session.getOpenSessions().toString());
		Thread.sleep(1000);
		connections.add(this);
		//broadcast(toJSON("BOT",MESSAGE_TYPE_CONNEXION, nickname));
		// String message = String.format("* %s %s", nickname, "has joined.");
		// broadcast(message);
		if (session.getOpenSessions().size() == NB_JOUEURS)
		{
			// Class.forName("org.postgresql.Driver");

			// Connection db =
			// DriverManager.getConnection("jdbc:postgresql://192.168.1.12:5432/bouakbase",
			// "postgres", "postgres");
			ResultSet resultats = null;
			InitialContext cxt = new InitialContext();
			DataSource ds = (DataSource) cxt
					.lookup("java:/comp/env/jdbc/postgres");

			if (ds == null)
			{
				throw new Exception("Data source not found!");
			}

			String requete = "SELECT id_question, enonce, rep_nom, rep_prenom, rep_surnom, obligatoire FROM bouaksite.questions order by random() limit 2";
			Connection db = ds.getConnection();
			try
			{

				Statement stmt = db.createStatement();
				resultats = stmt.executeQuery(requete);

			} catch (SQLException e)
			{
				// traitement de l'exception
			}

			// parcours des données retournées
			try
			{
				// ResultSetMetaData rsmd = resultats.getMetaData();
				// int nbCols = rsmd.getColumnCount();
				boolean encore = resultats.next();
				String lj="";
				String lq="";
				while (encore)
				{
					//System.out.println(resultats);
					questionCourante.setId(resultats.getString("id_question"));
					questionCourante.setEnonce(resultats.getString("enonce"));
					questionCourante.setNom(resultats.getString("rep_nom"));
					questionCourante.setPrenom(resultats.getString("rep_prenom"));
					questionCourante.setSurnom(resultats.getString("rep_surnom"));
					questionCourante.setPrenomNeeded(resultats.getBoolean("obligatoire"));
					questions.put(String.valueOf(qnum), new Qreponse(questionCourante));
					lq=lq+questionCourante.getId()+";";
					lj=lj+questionCourante.getId()+";";
					qnum++;
					question = questionCourante.getEnonce();
					tableau = decoupe(question, 15);
					duree = question.length() * 50;
					encore = resultats.next();
				}
				resultats.close();
				db.close();
				ds = null;
				qnum=1;
				XStream xstream = new XStream(new SunUnsafeReflectionProvider(new FieldDictionary(new ImmutableFieldKeySorter())),new DomDriver("utf-8"));
				System.out.println(xstream.toXML(questions));
			} catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}

			Timer minuteur = new Timer();
			TimerTask tache = new TimerTask()
			{
				public void run()
				{
					if (ind < tableau.length && !questionFinie)
					{
						String msg = tableau[ind];
						broadcast(toJSON("BOT", MESSAGE_TYPE_ENONCE, msg));
						ind++;
					} else
					{
						broadcast(toJSON("BOTREP", MESSAGE_TYPE_BOT,
								"la réponse est : " + questionCourante.getPrenom() + " "
										+ questionCourante.getNom()));
						broadcast (toJSON("BOT", MESSAGE_TYPE_INFO,"fini"));
						cancel();
					}
				}
			};
			minuteur.schedule(tache, 0, (duree / 15 - 500));

		} else if (session.getOpenSessions().size() < NB_JOUEURS)
		{
			//broadcast("ajouté " + nickname);
		} else
		{
			//broadcast("Vous êtes trop nombreux, déco en cours...");
			end();
		}
	}

	@OnClose
	public void end()
	{
		connections.remove(this);
		String message = String
				.format("* %s %s", nickname, "has disconnected.");
		broadcast(message);
	}

	@OnMessage
	public void incoming(String message)
	{
		log.info(nickname+questionFinie);
		// Never trust the client
		String filteredMessage = HTMLFilter.filter(message.toString());
		log.info(message);
		log.info(filteredMessage);
		JsonReader jsonReader = Json.createReader(new StringReader(
				message));
		JsonObject j = jsonReader.readObject();
		if (j.getString("sender") != "##bouakbot##")
		{
			questionCourante = questions.get(String.valueOf(qnum));
			//System.out.println(j.toString());
			String resultat = questionCourante.getNom();
			String iResultat = resultat;
			if (questionCourante.isPrenomNeeded())
			{
				resultat = questionCourante.getPrenom() + " " + questionCourante.getNom();
				iResultat = questionCourante.getNom() + " " + questionCourante.getPrenom();
			}
			DamerauLevenshtein damlev = new DamerauLevenshtein(message.toString().toLowerCase(), resultat.toLowerCase());
			DamerauLevenshtein damlev2 = new DamerauLevenshtein(message.toString().toLowerCase(), iResultat.toLowerCase());
	
			if ((Math.min(damlev.getSimilarity(), damlev2.getSimilarity())) > 2)
			{
				broadcast(toJSON(nickname, MESSAGE_TYPE_REPONSE, message.toString()));
				broadcast(toJSON("BOTREP", MESSAGE_TYPE_BOT, "Mauvaise Réponse"));
				log.info("vous avez faux, la réponse est : "+ questionCourante.getPrenom() + " " + questionCourante.getNom());
			} else
			{
				broadcast(toJSON(nickname, MESSAGE_TYPE_REPONSE, message.toString()));
				broadcast(toJSON("BOTREP", MESSAGE_TYPE_BOT, "VOUS AVEZ TROUVE, BRAVO"));
				log.info("Vous avez trouvé !!!");
				questionFinie = true;
			}
		}
	}

	@OnError
	public void onError(Throwable t) throws Throwable
	{
		log.error("Chat Error: " + t.toString(), t);
	}

	public String[] decoupe(String entree, int nbParties)
	{
		String[] tableau = new String[nbParties];
		int longueur = entree.length();
		int indice = 0;
		int partSize = Math.round((float) (longueur / nbParties));
		String partieTemp = "";
		for (int i = 0; i < nbParties - 1; i++)
		{
			partieTemp = entree.substring(indice, indice + partSize);
			indice = indice + partSize;
			tableau[i] = partieTemp;
		}
		tableau[nbParties - 1] = entree.substring(indice);
		return tableau;

	}

	private void broadcast(String msg)
	{
		System.out.println("openses:"+session.getOpenSessions().toString());
		for (Session ses : session.getOpenSessions())
		{
			try
			{
				if (ses.getUserProperties().get("partie") == numPartie)
				{
					ses.getBasicRemote().sendText(msg);
					System.out.println(new Date().toString());
				}
				
			} catch (IOException e)
			{
				log.debug("Chat Error: Failed to send message to client", e);
				try
				{
					ses.close();
				} catch (IOException e1)
				{
					// Ignore
				}
				String message = String.format("* %s %s", nickname,
						"has been disconnected.");
				broadcast(message);
			}
		}
	}


	public static String toJSON(String sender, String type, String content)
	{
		JsonObject jObject = Json.createObjectBuilder().build();

		jObject = Json.createObjectBuilder().add("sender", sender).add("type",type)
				.add("content", content).build();
		return jObject.toString();
	}

	public static String QrepToJSON(String sender, Qreponse question)
	{
		JsonObject jObject = Json.createObjectBuilder().build();

		jObject = Json
				.createObjectBuilder()
				.add("sender", sender)
				.add("content",
						Json.createObjectBuilder()
								.add("enonce", question.getEnonce())
								.add("nom", question.getNom())
								.add("prenom", question.getPrenom())
								.add("surnom", question.getSurnom())
								.add("obligatoire", question.isPrenomNeeded()))
				.build();
		return jObject.toString();
	}
	public void creerPartie(String lj, String lq)
	{
		String requete = "insert into bouaksite.partie(nbjoueurs, liste_joueurs, liste_questions) values(2,'"+lj+"','"+lq+"')";
	}
	public int deadline(int length)
	{
		return length * 50;
	}

}
