package util;

import java.sql.Connection;

public class BouakStats
{
	private int joueur;
	private int question;
	private int partie;
	private int score;
	
	public void addToBase(Connection conn)
	{
		String requete = "UPDATE joueurs set nbp=nbp+1, nbq=nbq+1, nbqok=nbqok+1, ptstotal=ptstotal+6";
	}
}
