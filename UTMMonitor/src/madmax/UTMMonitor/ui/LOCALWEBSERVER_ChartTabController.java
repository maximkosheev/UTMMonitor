package madmax.UTMMonitor.ui;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;

public class LOCALWEBSERVER_ChartTabController extends ChartTabController {

	@FXML
	protected Button btnSettings;
	
	@Override
	public void initialize(Node ui)
	{
		super.initialize(ui);
		
		btnSettings.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				TextInputDialog dialog = new TextInputDialog("localwebserver");
				dialog.setTitle("local web server address");
				dialog.setHeaderText("Look, a text input dialog");
				dialog.setContentText("Please enter local web server address");
				
				Optional<String> result = dialog.showAndWait();
				
				if (result.isPresent())
					System.out.println(result.get());
			}
		});
		btnSettings.setVisible(false);
	}
}
