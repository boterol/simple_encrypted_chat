import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import Demo.*;
import java.net.InetAddress;
import java.util.Random;
import java.io.*;
import javafx.application.Platform;
import javafx.stage.Stage;


/**
 * This is the main class that is compiled by "gradle build" command
 * This class contains the main conection to the server and the main method to run the client.
 * 
 * Read the documentation cautiously to understand the code.
 */
public class Client{
	public static final int[] primeNumbers = {
        5, 7, 11, 13, 17, 19, 23
    };

	private static ChatUI chatUI;

	private static int strangersValue=-1;

	
	public static void main(String[] args){
		// This try block is used to initialize the connection with the server by using the information in the client.cfg file.
		try (Communicator communicator = Util.initialize(args, "client.cfg")){
			
			try{
				// This line is used to get the proxy of the server. The server proxy must be activated first in server side
				// Note that "ChatServerPrx" class is not declared anywhere, because it is inferred
				// by the Ice Middleware over the EncryptedChat.ice file.
				ChatServerPrx chatServerPrx = ChatServerPrx.checkedCast(communicator.propertyToProxy("ChatServer.Proxy"));
				
				if (chatServerPrx == null){ // If the conection fails. This error might be thrown because of the port or the IP of the server.
					throw new Error("Invalid proxy");
				}

				// Inicia JavaFX en un hilo separado
				Platform.startup(() -> {
					chatUI = new ChatUI();
					chatUI.setChatServerPrx(chatServerPrx);
					chatUI.start(new Stage());
				});

				//This is the adapter for the chat client this name can be anything as long as it matches with the name used in the client.cfg file
				com.zeroc.Ice.ObjectAdapter adapter = communicator.createObjectAdapter("Chat.Client"); 

				//This is the object that will be used to create the proxy for the client. ChatClientI is the class that implements the ChatClient interface.
				//There is no need to import the ChatClientI class because it is in the same package as the Client class.
				com.zeroc.Ice.Object object = new ChatClientI(chatUI);

				//This line is used to add the object to the adapter, wich is the object that will be used to create the proxy for the client.
				adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("chatClient"));
				adapter.activate();

				//This line is used to create the proxy for the client. The proxy must be created and activated for it to be able to
				//subscribe to the list of clients in the server side.
				ChatClientPrx client =
				ChatClientPrx.uncheckedCast(adapter.createProxy(
                com.zeroc.Ice.Util.stringToIdentity("chatClient")));


				//This try block is used to get the input from the user and send it to the server.
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

					// This line is used to get the hostname of the client. The hostname is used to identify the client in the server side.
					String hostname = InetAddress.getLocalHost().getHostName();
					System.out.println("HOSTNAME: "+hostname);
					int client_number = chatServerPrx.getClientCount();
					hostname = "user" + "@" + hostname + "_" + client_number;

					chatUI.setHostname(hostname);

					// This line is used to register the client in the server side. The client must be registered in the server side
					chatServerPrx.registerClient(hostname, client); // This is the way the server admits any amount of clients.


					//----------PROTOCOLO DE ENCRIPCIÓN----------
					System.out.println("--------------Encryption Protocol Started--------------");
					System.out.println("Waiting for another client to connect...");

					int secretValue=generateSecretValue(chatServerPrx);

					System.out.println("SecretValue: "+secretValue);
					//secretValue=-1 means that there was a timeout reaching the other clients value.
					//secretValue!=-1 means succes at stablishing the protocol 
					if(secretValue!=-1){
						String userInput = "Default text";
						System.out.print("Enter a message (type 'exit' to quit): ");
	
						while ((userInput = reader.readLine()) != null) {
							String result = chatServerPrx.sendMessage(userInput+"-"+hostname);
	
							System.out.println(result);
	
							if (userInput.equalsIgnoreCase("exit")){
								break;
							}
	
							System.out.print("Enter a message (type 'exit' to quit): ");
						}
					}

					// This line is used to unregister the client in the server side. The client must be unregistered in the server side
					chatServerPrx.unregisterClient(hostname);

				} catch (Exception e) {

					e.printStackTrace();

				}
			} catch (com.zeroc.Ice.ConnectionTimeoutException e){
				System.out.println("-------[WARNIG]------- \n Timeout!");
				e.printStackTrace();
			}
		}
	}

	private static int generateSecretValue(ChatServerPrx chatServerPrx){
		//gets g and n public values from server 
		String strGN=chatServerPrx.getGN();
		System.out.println("G,N: "+strGN);
		String[] gn=strGN.split(",");

		//computates the first step of the deffie hellman protocol.
		//myValue=g^x mod n
		int g=Integer.parseInt(gn[0]);
		int n=Integer.parseInt(gn[1]);
		int x = new Random().nextInt(40)+8;
		
		int myValue=DeffieHellman.firstComputation(g, n, x);
		
		System.out.println("MYVALUE: "+myValue);
		//sends it to the server so the other client can acces it
		chatServerPrx.SetProtocolValues(myValue);
		
		System.out.println("G: "+g+" N: "+n+" X: "+x);

		//verifies if the other client has already uploaded his value to the server.
		//if the other client has, the value gets processed and stored in the variable strangersValue.
		//if the other client has not, it retries.
		long startTime = System.currentTimeMillis();
		long timeout = 60000; // 1 minute

		while (strangersValue == -1) {
			if (System.currentTimeMillis() - startTime > timeout) {
				System.out.println("Timed out waiting for stranger's value.");
				return -1;
			}
			try {
				String strValues=chatServerPrx.getProtocolValues();
				String[] splitted=strValues.split(",");
				if(splitted.length>1){
					int counter=0;
					for(String str: splitted){
						counter++;
						if(Integer.parseInt(str) != myValue){
							strangersValue=Integer.parseInt(str);
							break;
						} else if(counter==2){
							strangersValue=myValue;
						}
					}
				}
				Thread.sleep(1000); // Check every 0.5s
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//secret value computation: strangersValue^x mod n
		int secretValue=DeffieHellman.computeSecretValue(strangersValue, n, x);

		return secretValue;
	}

}
