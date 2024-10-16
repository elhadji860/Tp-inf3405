import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.IOException;
import java.io.NotActiveException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Server {
	private static ServerSocket Listener;
	
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
		int clientNumber = 0;
		
		InetAddress serverIP = getIP();
		int serverPort = getPort();
		
		Listener = new ServerSocket();
		Listener.setReuseAddress(true);
		
		
		while (true) {
			try 
			{
				System.out.println("connecting...");
				Listener.bind(new InetSocketAddress(serverIP, serverPort));
				break;
			}
			catch(IOException e) 
			{
				System.out.format("%s - binding error \nNouvel essaie\n", e.getMessage());
				serverPort = getPort();
				serverIP = getIP();
				
			}
			catch(IllegalArgumentException e) 
			{
				System.out.format("%s - binding error \nNouvel essaie\n", e.getMessage());
				serverPort = getPort();
				serverIP = getIP();
				
			}
		
			catch(SecurityException e) 
			{
				System.out.format("%s - binding error \nNouvel essaie\n", e.getMessage());
				serverPort = getPort();
				serverIP = getIP();
				
			}
		
		
		}
		System.out.format("the server is running on %s:%d%n", serverIP.getHostAddress(), serverPort);
		try {
			
			
			while(true) {
				new ClientHandler(Listener.accept(), clientNumber++).start();
			}
			
		}
		finally {
			Listener.close();
		}
	}
}
