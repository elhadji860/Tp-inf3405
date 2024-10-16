import java.io.DataInputStream;
import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.EOFException;
import java.io.IOException;
import java.io.NotActiveException;
//import java.io.UTFDataFormatException;
import java.net.InetAddress;
import java.net.Socket;
//import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;
// Application client
public class Client {
	private static Socket socket;
	
	private static InetAddress getIP(){
		Scanner input = new Scanner(System.in);
		System.out.print("rentrer l'adresse Ip du serveur : ");
		
		String entree = input.nextLine();
		boolean invalide = true;
		
		while (invalide) {
		
			try 
			{
				InetAddress IpAddress = InetAddress.getByName(entree);
				return IpAddress;
			}
			catch(UnknownHostException e)
			{
			System.out.format("%s - rentrer une addresse Ip valide: ", e.getMessage());
			entree = input.nextLine();
			}
			catch(SecurityException e)
			{
			System.out.format("%s - rentrer une addresse Ip valide: ", e.getMessage());
			entree = input.nextLine();
			}
		
		}
		
		input.close();
		return null ;
	}
	
	private static int getPort() {
		
		int port = 0;
		Scanner entree = new Scanner(System.in);
		System.out.print("rentrer le numero du port: ");
		boolean invalide = true;
		while(invalide) {
			try 
			{
				port = Integer.parseInt(entree.nextLine());
				if (port < 5000 || port > 5050) {
					throw new NotActiveException("Valeur invalide");
				}
				
				return port;
			}
			catch(NotActiveException e)
			{
				System.out.format("%s - rentrer une valeur valide: ", e.getMessage());
			}
			catch(NoSuchElementException e)
			{
				System.out.format("%s - rentrer une valeur valide: ", e.getMessage());
			}
			catch(IllegalStateException e)
			{
				System.out.format( e.getMessage());
				System.exit(1);
			}
			catch(NumberFormatException e)
			{
				System.out.format("%s - rentrer une valeur valide: ", e.getMessage());
			}


		}
		entree.close();
		return port;
	}
	
	public static void main(String[] args) throws Exception {

		InetAddress serverAddress = getIP();
		int port = getPort();
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			try 
			{
				System.out.println("connecting...");
				socket = new Socket(serverAddress, port);
				System.out.format("Serveur lancé sur [%s:%d] \n", serverAddress, port);
				break;
			}
			catch(IllegalArgumentException e)
			{
				System.out.println("erreur de connexion");
				System.out.println("nouvel essai : ");
				serverAddress = getIP();
				port = getPort();
			}
			catch(IOException e) 
			{
				System.out.println("erreur de connexion");
				System.out.println("nouvel essai : ");
				serverAddress = getIP();
				port = getPort();
			}
			catch(SecurityException e) 
			{
				System.out.println("erreur de connexion");
				System.out.println("nouvel essai : ");
				serverAddress = getIP();
				port = getPort();
			}
		}
		while (true){
			String commande;
			System.out.print("rentrer une commande: ");
			commande = sc.nextLine();
			
			try {
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(commande);
				try 
				{
					DataInputStream in = new DataInputStream(socket.getInputStream());
					
					String messageFromServer = null;
					while(messageFromServer == null) {
						messageFromServer = in.readUTF();
					}
					if(messageFromServer.equals("exit")) {
						System.exit(0);
					}
								
					else {
					System.out.format("%s \n",messageFromServer);
					}
				}
				catch (IOException e) {
					System.out.println("erreur de communication");
					System.exit(0);
				}
				
			}
			catch (IOException e) {
				System.out.println("erreur de communication");
				break;
			}
			
		}
		
		sc.close();
		socket.close();
		System.exit(0);
	}
}