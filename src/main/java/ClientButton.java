import javafx.scene.control.Button;

public class ClientButton extends Button {

	public int clientNum;
	public boolean isClicked = false;
		
	public ClientButton(String name, int count) {
			
		super();
			
		this.clientNum = count;

		setText(name);
		setStyle("-fx-base: green;");
			
	}
		
}
