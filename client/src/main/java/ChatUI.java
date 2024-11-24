import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Demo.*;

public class ChatUI extends Application {

    private TextArea chatArea;  // Área para mostrar los mensajes
    private TextField inputField;  // Campo de entrada para escribir mensajes
    private ChatServerPrx chatServerPrx;  // Proxy para interactuar con el servidor
    private String hostname;

    public static void main(String[] args) {
        launch(args);  // Inicia la aplicación JavaFX
    }

    @Override
    public void start(Stage stage) {
        // Layout principal
        VBox root = new VBox();
        root.setSpacing(10);  // Espacio entre elementos

        // Área de chat (deshabilitada para edición)
        chatArea = new TextArea();
        chatArea.setEditable(false);  // Solo lectura
        chatArea.setWrapText(true);  // Ajusta el texto automáticamente

        // Scroll para el chat
        ScrollPane scrollPane = new ScrollPane(chatArea);
        scrollPane.setFitToWidth(true);

        // Campo de entrada
        inputField = new TextField();
        inputField.setPromptText("Escribe un mensaje...");

        // Botón para enviar mensajes
        Button sendButton = new Button("Enviar");
        sendButton.setOnAction(event -> sendMessage());

        // Agregar elementos al layout
        root.getChildren().addAll(scrollPane, inputField, sendButton);

        // Configuración de la escena
        Scene scene = new Scene(root, 400, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
        
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isEmpty()) {
            try {
                // Envía el mensaje al servidor
                String result = chatServerPrx.sendMessage(message + "-" + hostname);
                
                // Actualiza el área del chat
                chatArea.appendText("Tú: " + message + "\n");
                
                // Limpiar el campo de texto
                inputField.clear();
                
                // Mostrar respuesta del servidor
                chatArea.appendText(result + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Método para recibir mensajes del servidor
    public void receiveMessage(String msg) {
        chatArea.appendText(msg + "\n");
    }

    public void setChatServerPrx(ChatServerPrx chatServerPrx) {
        this.chatServerPrx = chatServerPrx;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
