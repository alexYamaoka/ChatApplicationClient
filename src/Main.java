import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main extends Application
{

    private final int SERVER_PORT = 8000;



    // IO Streams
    DataOutputStream toServer;
    DataInputStream fromServer;

    Socket socket;






    @Override
    public void start(Stage primaryStage) throws Exception
    {
        // Panel to hold the label and text field to enter message
        BorderPane paneForTextField = new BorderPane();

        paneForTextField.setPadding(new Insets(5,5,5,5));
        paneForTextField.setStyle("-fx-border-color: green");
        paneForTextField.setLeft(new Label("Enter Message: "));


        TextField textField = new TextField();
        textField.setAlignment(Pos.BOTTOM_RIGHT);
        paneForTextField.setCenter(textField);







        // Main border pane to hold text field to enter message and chat history
        BorderPane mainPane = new BorderPane();

        // text area to display contents
        TextArea textArea = new TextArea();

        mainPane.setCenter(new ScrollPane(textArea));
        mainPane.setTop(paneForTextField);




        // create a scene and place it in the stage
        Scene scene = new Scene(mainPane, 450, 200);

        primaryStage.setTitle("Chat Application Client");
        primaryStage.setScene(scene);
        primaryStage.show();


        // connect to server
        try
        {

            // create a socket to connect to the server
            socket = new Socket("localhost", 8000);


            // create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());


            // create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());



        }
        catch (IOException ex)
        {
            textArea.appendText(ex.toString() + '\n');
        }






        // sendMessage Thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                // event handler for pressing enter on keyboard for sending message
                textField.setOnAction( e ->
                {
                    try
                    {

                        // get message from the text field
                        String message = textField.getText().trim();

                        // send the message to the server
                        toServer.writeUTF(message);
                        toServer.flush();


                        Platform.runLater( () ->
                        {
                            textField.setText("");
                        });


                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }

                });

            }
        });



        // readMessage Thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                while (true)
                {
                    try
                    {
                        // read the message sent to this client
                        String message = fromServer.readUTF();
                        System.out.println(message);


                        Platform.runLater( () ->
                        {
                            textArea.appendText(message + '\n');
                        });

                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }

            }
        });






        sendMessage.start();
        readMessage.start();












    }




    public static void main(String[] args) {
        launch(args);
    }
}













