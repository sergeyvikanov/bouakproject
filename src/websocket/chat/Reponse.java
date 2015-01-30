package websocket.chat;

public class Reponse {
	private String origine;
	private String contenu;
	
	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public Reponse(String origine, String contenu) {
		super();
		this.origine = origine;
		this.contenu = contenu;
	}

	@Override
	public String toString() {
		return "Reponse [origine=" + origine + ", contenu=" + contenu + "]";
	}

	public Reponse() {
		
		// TODO Auto-generated constructor stub
	}


}
