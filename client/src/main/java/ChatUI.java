import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;
import Demo.*;

public class ChatUI extends Application {

    private VBox chatBox;  // Contenedor para los mensajes del chat
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
        root.setPadding(new Insets(10));
        root.setSpacing(10);  // Espacio entre elementos

        // Caja para los mensajes del chat
        chatBox = new VBox();
        chatBox.setSpacing(10);  // Espacio entre mensajes

        // ScrollPane para hacer que los mensajes sean desplazables
        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Siempre muestra el scroll
        scrollPane.setFitToHeight(true);

        // Ajustar el contenido para que siempre haga scroll hacia abajo
        scrollPane.vvalueProperty().bind(chatBox.heightProperty());

        // Campo de entrada
        inputField = new TextField();
        inputField.setPromptText("Type a message...");
        inputField.setOnAction(event -> sendMessage()); // Enviar con Enter

        // Botón para enviar mensajes
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> sendMessage());

        // Crear un HBox para el input y el botón
        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(inputField, sendButton);
        inputBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(inputField, Priority.ALWAYS);  // Expande el campo de texto

        // Agregar elementos al layout
        root.getChildren().addAll(scrollPane, inputBox);
        VBox.setVgrow(scrollPane, Priority.ALWAYS); // Asegurar que el ScrollPane crezca para ocupar el espacio restante

        // Configuración de la escena
        Scene scene = new Scene(root, 400, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }

    private void sendMessage() {
        String message_original = inputField.getText();
        if (!message_original.isEmpty()) {
            try {
                // Envía el mensaje al servidor
                String message = AESMaganement.encryptMessage(Client.hashedKey, message_original);
                chatServerPrx.sendMessage(message + "696969" + hostname);
                
                // Añadir el mensaje del usuario a la ventana del chat
                addMessage("You: " + message_original, true);  // Mensaje propio

                // Limpiar el campo de texto
                inputField.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Método para recibir mensajes del servidor
    public void receiveMessage(String msg) {
        addMessage(msg, false);  // Mensaje de otro usuario
    }

    public void setChatServerPrx(ChatServerPrx chatServerPrx) {
        this.chatServerPrx = chatServerPrx;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    // Añadir un mensaje al chat con burbuja y alineación adecuada
    private void addMessage(String message, boolean isOwnMessage) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);  // Permite que el texto se ajuste a la ventana
        
        // Estilo diferente para los mensajes del usuario y los recibidos
        if (isOwnMessage) {
            messageLabel.getStyleClass().add("own-message");
        } else {
            messageLabel.getStyleClass().add("received-message");
        }

        // Crear un HBox para la alineación de los mensajes
        HBox messageContainer = new HBox();
        if (isOwnMessage) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);  // Alinear a la derecha
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);  // Alinear a la izquierda
        }
        messageContainer.getChildren().add(messageLabel);

        chatBox.getChildren().add(messageContainer);
    }
}
