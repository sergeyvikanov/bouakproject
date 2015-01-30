package util;

public interface Bordel
{
	/**
	 * Type des messages
	 * ENO = enoncé (par parties)
	 * BOT = Messages de contrôle du bot (reponse ok, non ok...) 
	 * INT = interne (pour la mecanique du jeu)
	 * REP = réponses, propositions des joueurs
	 * INF = Infos (fin d'un énoncé, question suivante, etc)
	 * CON = connexion/deconnexion des joueurs
	 */
	static final String MESSAGE_TYPE_ENONCE = "ENO";
	static final String MESSAGE_TYPE_BOT = "BOT";
	static final String MESSAGE_TYPE_INTERNE = "INT";
	static final String MESSAGE_TYPE_REPONSE = "REP";
	static final String MESSAGE_TYPE_INFO = "INF";
	static final String MESSAGE_TYPE_CONNEXION = "CON";
	static final String MESSAGE_TYPE_PRET = "RDY";

	static final int NB_JOUEURS = 1;
	static final String adresse = "/websocket/chat/{numero}";
}
