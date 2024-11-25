import Demo.*;
import com.zeroc.Ice.Current;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the ChatServer interface and is used to handle the
 * communication between the clients and the server. This class is responsible
 * for registering and unregistering clients, sending messages to all clients,
 * and executing commands on the server.
 * 
 * Every method declared in the interface must have "current" as the last parameter.
 */
public class ChatServerI implements ChatServer {

  ArrayList<Integer> protocolPublicValues = new ArrayList<>();
  
  @Override
  public void unregisterClient(String hostName, com.zeroc.Ice.Current current) {
    //new client disconnected
    Server.unregisterClient(hostName);
  }


  @Override
  public void registerClient(String hostName, ChatClientPrx callback, com.zeroc.Ice.Current current) {
    //new client connected
    Server.registerClient(hostName, callback);
  }

  //sets the public calculted values fot the 2 clients to acces in the global vaiable protocolValues
  @Override
  public void SetProtocolValues(int value, com.zeroc.Ice.Current current) {
    protocolPublicValues.add(value);
  }

  //gets the values ans returns them as sting because ice do not support array data types. 
  //this values will be processed in the client side
  @Override
  public String getProtocolValues(com.zeroc.Ice.Current current) {
    String output="";
    if(protocolPublicValues.size()==2){
      output=protocolPublicValues.get(0)+","+protocolPublicValues.get(1);
    } else if (protocolPublicValues.size()>0){
      output=protocolPublicValues.get(0).toString();
    }
    return output;
  }

  //gets the public G and N values. 
  @Override
  public String getGN(com.zeroc.Ice.Current current){
    
    return String.valueOf(Server.gn[0])+","+String.valueOf(Server.gn[1]);
  }

  /**
   * This method is used to execute a command on the server. The command is
   * received as a string and is executed using the ProcessBuilder class. The
   * output of the command is returned as a string.
   */
  @Override
  public void shutdown(com.zeroc.Ice.Current current) {
    System.out.println("Shutting down...");
    try {
      current.adapter.getCommunicator().shutdown();
    } catch (com.zeroc.Ice.LocalException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method is used to send a message to all clients. The message is
   * received as a string and executes the the broadCastMessage method.
   */
  @Override
  public String sendMessage(String msg, com.zeroc.Ice.Current current) {
      String clientHN = msg.split("696969")[1];
      msg = msg.split("696969")[0];

      final String finalMsg = msg;
      final String finalClientHN = clientHN;

      if (msg.length() == 0) {
          return msg;
      }

      System.out.println(String.format("\n[NEW] - %s said: \"%s\"\n", clientHN, msg));

      broadCastMessage(clientHN + ": " + msg, clientHN);

      return ""; //clientHN + " (Me): " + msg
  }

  @Override
  public int getClientCount(com.zeroc.Ice.Current current){
    return Server.getClientCount();
  }

  
  /**
   * This method is used to send a message to all clients. The message is
   * received as a string and is sent to all clients using the receiveMessage
   * method.
   */
  private void broadCastMessage(String msg, String clientHN) {
    Map<String, ChatClientPrx> clients = Server.getAllChatClients();
    for (String hostname : clients.keySet()) {
      if(!hostname.equals(clientHN)){
        try {
          clients.get(hostname).receiveMessage(msg);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * This method is used to execute a command on the server. The command is
   * received as a string and is executed using the ProcessBuilder class. The
   * output of the command is returned as a string.
   */
  private String executeCommand(String command) {
    String output = "";
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
      processBuilder.redirectErrorStream(true);

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream())
      );
      String line;
      while ((line = reader.readLine()) != null) {
        output = output + line + "\n";
      }

      int exitCode = process.waitFor();

      output =
        output + "El comando ha terminado con código de salida: " + exitCode;
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
      return "No fue posible ejecutar el comando";
    }
    return output;
  }
  
}
