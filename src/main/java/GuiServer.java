import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application{

	
	TextField s1,s2,s3,s4, c1;
	Button serverChoice,clientChoice,b1, b2;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox, h1;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	Boolean privateChat = false;
	
	ListView<String> listItems, listItems2;
	ListView<ClientButton> clientsActive;
	ArrayList<Integer> recievers = new ArrayList<Integer>();
	ArrayList<Integer> clientList = new ArrayList<Integer>();
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("The Networked Client/Server GUI Example");
		
		this.serverChoice = new Button("Server");
		this.serverChoice.setStyle("-fx-pref-width: 300px");
		this.serverChoice.setStyle("-fx-pref-height: 300px");
		
		this.serverChoice.setOnAction(e->{ primaryStage.setScene(sceneMap.get("server"));
											primaryStage.setTitle("This is the Server");
				serverConnection = new Server(data -> {
					Platform.runLater(()->{
						listItems.getItems().add(data.toString());
					});

				});
											
		});
		
		
		this.clientChoice = new Button("Client");
		this.clientChoice.setStyle("-fx-pref-width: 300px");
		this.clientChoice.setStyle("-fx-pref-height: 300px");
		
		this.clientChoice.setOnAction(e-> {
			primaryStage.setScene(sceneMap.get("client"));
			primaryStage.setTitle("This is a client");
			clientConnection = new Client(data->{
				ClientInfo info = (ClientInfo)data; // from client
				Platform.runLater(()->{
					if (info.updateActiveClientList) {
						clientsActive.getItems().clear();
						clientList.clear();
						for (int i : info.clientsActive) {
							ClientButton b = new ClientButton("Client #" + i, i);
							b.setOnAction(new ButtonClick());
							clientsActive.getItems().add(b);
							clientList.add(i);
						}
						listItems2.getItems().add(info.message);
					} else {
						listItems2.getItems().add(info.message);
					}
				});
			});
							
			clientConnection.start();
		});
		
		this.buttonBox = new HBox(400, serverChoice, clientChoice);
		startPane = new BorderPane();
		startPane.setPadding(new Insets(70));
		startPane.setCenter(buttonBox);
		
		startScene = new Scene(startPane, 800,800);
		
		listItems = new ListView<String>();
		listItems2 = new ListView<String>();
		
		c1 = new TextField();
		b1 = new Button("Send");
		b1.setOnAction(e->{
			ClientInfo info = new ClientInfo();
			info.message = c1.getText();
			
			if (recievers.size() == 0) {
				clientConnection.send(info);
				c1.clear();
			} else {
				info.privateChat = privateChat;
				info.recievers = recievers;
				clientConnection.send(info);
				c1.clear();
			}
			
		});
		
		b2 = new Button("EXIT PRIVATE CHAT");
		b2.setOnAction(e->{
			privateChat = false;
			clientsActive.getItems().clear();
			for(int i : clientList) {
				ClientButton b = new ClientButton("Client #" + i, i);
				b.setOnAction(new ButtonClick());
				clientsActive.getItems().add(b);
			}
			recievers.clear();
			
		});
		
		sceneMap = new HashMap<String, Scene>();
		
		sceneMap.put("server",  createServerGui());
		sceneMap.put("client",  createClientGui());
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		
		 
		
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}
	
	public Scene createServerGui() {
		
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: coral");
		
		pane.setCenter(listItems);
	
		return new Scene(pane, 500, 400);

	}
	
	public Scene createClientGui() {

		clientsActive = new ListView<ClientButton>();
		listItems2.setPrefWidth(450);
		
		Label l1 = new Label();
		l1.setText("- Select clients to send (group) messages");
		Label l2 = new Label();
		l2.setText("- Sends message to everyone by default");
		
		l1.setStyle("-fx-text-fill: white;");
		l2.setStyle("-fx-text-fill: white;");
		
		
		h1 = new HBox(listItems2,clientsActive);
		h1.setAlignment(Pos.CENTER);
		
		clientBox = new VBox(10, c1,b1,b2,l1,l2,h1);
		clientBox.setStyle("-fx-background-color: blue");
		return new Scene(clientBox, 400, 400);
		
	}
	
	public class ButtonClick implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			
			ClientButton client = (ClientButton) event.getSource();
			
			client.isClicked = true;
			recievers.add(client.clientNum);
			privateChat = true;
			
			client.setStyle("-fx-base: blue");
			
		}
		
	}

}
