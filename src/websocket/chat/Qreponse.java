package websocket.chat;


/** 
Objet Utilisé pour conserver les valeurs d'une réponse à un énoncé (en base)
**/
public class Qreponse {
	private String id;
	private String enonce;
	private String nom;
	private String prenom;
	private String surnom;
	private boolean prenomNeeded;
	
	
	public Qreponse(String id, String enonce, String nom, String prenom,
			String surnom, boolean prenomNeeded)
	{
		super();
		this.id = id;
		this.enonce = enonce;
		this.nom = nom;
		this.prenom = prenom;
		this.surnom = surnom;
		this.prenomNeeded = prenomNeeded;
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getEnonce()
	{
		return enonce;
	}
	public void setEnonce(String enonce)
	{
		this.enonce = enonce;
	}
	public String getNom()
	{
		return nom;
	}
	public void setNom(String nom)
	{
		this.nom = nom;
	}
	public String getPrenom()
	{
		return prenom;
	}
	public void setPrenom(String prenom)
	{
		this.prenom = prenom;
	}
	public String getSurnom()
	{
		return surnom;
	}
	public void setSurnom(String surnom)
	{
		this.surnom = surnom;
	}
	public boolean isPrenomNeeded()
	{
		return prenomNeeded;
	}
	public void setPrenomNeeded(boolean prenomNeeded)
	{
		this.prenomNeeded = prenomNeeded;
	}
	
	public Qreponse()
	{
		super();
		this.id = null;
		this.enonce = null;
		this.nom = null;
		this.prenom = null;
		this.surnom = null;
		this.prenomNeeded = false;
	}
	public Qreponse(Qreponse q)
	{
		this.id = q.id;
		this.enonce = q.enonce;
		this.nom = q.nom;
		this.prenom = q.prenom;
		this.surnom = q.surnom;
		this.prenomNeeded = q.prenomNeeded;
	}


}
