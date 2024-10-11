import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
//import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
//import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler extends Thread { // pour traiter la demande de chaque client sur un socket particulier
	private Socket socket; 
	private int clientNumber; 
	private String cheminCourant = ".";
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber; 
		System.out.println("New connection with client#" + this.clientNumber + " at" + socket);
		}
	
	String commandeValide(String command) {
		
		String commande = command;
		commande = commande.trim();
		
		String listeCommande[] = {"ls", "mkdir", "exit", "cd", "delete"};
		
		for(String val : listeCommande) {
			if (commande.startsWith(val)) {
				if (commande.equals("ls") || commande.equals("exit")) {
					return val;
				}
				if (commande.startsWith("ls") || commande.startsWith("exit") ) {
					return null;
				}
				commande = commande.replaceFirst(val, "");
				
				if (commande.startsWith(" ")) {
					commande = commande.trim();
					if(!commande.contains(" ") && !commande.equals("") ) {
						return val;
					}
				}
				return null;
			}
		}
		
		return null;
	}
	
	String commandInterpreter(String commande) {
		
		String command = commande;
		if (commandeValide(command) != null) {
			if (commandeValide(command).equals("ls")) {
				try 
				{
					File dir = new File(cheminCourant);
					String result = "";
					for( String x : dir.list()) {
						result = result + " " + x.replace(".", "") + "," ;
					}
					return result;
				}
				catch(SecurityException e)
				{
					return "erreur execution";
				}
			}
			else if (commandeValide(command).equals("mkdir")) {
				try 
				{
					command = command.replaceFirst("mkdir", "");
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
			else if (commandeValide(command).equals("exit")) 
			{
				return "exit";
			}
			
			else if (commandeValide(command).equals("cd")) 
			{
				try 
				{
					command = command.replaceFirst("cd", "");
					command = command.replace(" ", "");
					
					if (command.equals("..")) {
						if (cheminCourant.equals(".")) {
							String message = "execution reussie - repertoire courant : " + cheminCourant;
							return message;
						}
						String list[] = cheminCourant.split("/");
						cheminCourant = "";
						for (int x = 0; x < list.length-1; ++x) {
							cheminCourant += list[x] + "/";
						}
						cheminCourant = cheminCourant.substring(0, cheminCourant.length()-1);
						String message = "execution reussie - repertoire courant : " + cheminCourant;
						return message;
					}
					else
					{
						String nouveauChemin = cheminCourant + "/" +command ;
						File file = new File(nouveauChemin);
							
						if(file.exists()) {
							cheminCourant = nouveauChemin;
							String message = "execution reussie - repertoire courant : " + cheminCourant;
							return message;
								
						}
							
						return "erreur execution";
						
					}
						
				}
				catch(SecurityException e)
				{
					return "erreur execution";
				}
				
			}
			else if (commandeValide(command).equals("delete"))
			{
				try 
				{
					command = command.replaceFirst("delete", "");
					command = command.replace(" ", "");
					
					String dirToDelete = cheminCourant + "/" + command;
					File file = new File(dirToDelete);
					if(file.delete()) {
						return "execution reussie";
					};
					String message = "erreur execution - " + command + " n'est pas vide ou est inexistant"; 
					return message;
				}
				catch(SecurityException e)
				{
					return "erreur execution";
				}
				
			}
			else if (commandeValide(command).equals("upload")) {
				
			}
			
		}
		
		
		return "commande invalide";
	}
	
	public void run() { // Création de thread qui envoi un message à un client
			while(true) 
			{
				String commande = null;
				try 
				{
					DataInputStream in = new DataInputStream(socket.getInputStream());
					while(commande == null)
						commande = in.readUTF(); 
					 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");  
					 LocalDateTime now = LocalDateTime.now();  
					System.out.format("[%s : %s - %s ] : %s \n",socket.getInetAddress() , socket.getPort(),dtf.format(now) ,commande);
					
					try 
					{
						DataOutputStream out = new DataOutputStream(socket.getOutputStream());
						String message = commandInterpreter(commande);
						out.writeUTF(message);
						if (message.equals("exit")) {
							break;
						}
						
						
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
				catch(SecurityException e) {
					System.out.println("erreur de communication");
					break;
				}
				
				
			}
	
	}
}