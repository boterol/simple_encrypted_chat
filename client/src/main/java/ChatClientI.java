import com.zeroc.Ice.Current;
import javafx.application.Platform;
import Demo.*;

/**
 * This class implements the ChatClient interface defined in the file "CallBackModule.ice"
 */
public final class ChatClientI implements ChatClient {
    private ChatUI chatUI;

    public ChatClientI(ChatUI chatUI) {
        this.chatUI = chatUI;
    }

    /**
     * This method is used to receive a message from the server. This method will be referenced
     * in the server side to send messages to the client.
     * @param msg The message received from the server
     * @param current The current object
     */
    @Override
    public void receiveMessage(String msg, com.zeroc.Ice.Current current){   
        try {
            msg=msg.split(" ")[1];
            String decryptedMessage=AESMaganement.decryptMessage(Client.hashedKey, msg);
            System.out.println("ECNRYPTED: "+ msg+"\n");
            System.out.println("DECRYPTED: "+ decryptedMessage+"\n");
            if (chatUI == null) {
                return;
            }
            //Platform.runLater(() -> chatUI.receiveMessage(decryptedMessage));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}