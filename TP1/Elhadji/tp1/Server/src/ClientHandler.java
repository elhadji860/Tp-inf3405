import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		System.out.println("New connection with client#" + this.clientNumber + " at" + socket);
		}
	
	String commandeValide(String commande) {
		
		String listeCommande[] = {"ls", "mkdir"};
		
		for(String val : listeCommande) {
			if (commande.contains(val)) {
				return val;
			}
		}
		
		return null;
	}
	
	String commandInterpreter(String command, String cheminCourant ) {
		
		if (commandeValide(command) != null) {
			if (commandeValide(command) == "ls") {
				try 
				{
					File dir = new File(cheminCourant);
					System.out.println(dir.list());
					return "execution Reussie";
				}
				catch(SecurityException e)
				{
					return "erreur execution";
				}
			}
			else if (commandeValide(command) == "mkdir") {
				try 
				{
					command = command.replace("mkdir", "");
					command = command.replace(" ", "");
					command = cheminCourant + "/" + command;
					File dir = new File(command);
					if (dir.mkdir()) {
						return "execution Reussie"; 
					}
					else {
						System.out.println(cheminCourant);
						return "erreur execution";
					}
				}
				catch(SecurityException e)
				{
					return "erreur execution";
				}
			}
			
		}
		
		
		return "commande invalide";
	}
	
	public void run() { // Création de thread qui envoi un message à un client
			while(true) 
			{
				String cheminCourant = ".";
				String commande = null;
				try 
				{
					DataInputStream in = new DataInputStream(socket.getInputStream());
					while(commande == null)
						commande = in.readUTF(); 
					System.out.format("%s \n", commande);
					
					try 
					{
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						out.writeUTF(commandInterpreter(commande, cheminCourant ));
					}
					catch (IOException e) {
						System.out.println("erreur de communication");
						break;
					}
				}
				catch (IOException e) {
					System.out.println("erreur de communication");
					break;
				}
				
				
			}
	
	}
}