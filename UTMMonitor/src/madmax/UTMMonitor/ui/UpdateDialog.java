package madmax.UTMMonitor.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import madmax.UTMMonitor.Main;
import madmax.UTMMonitor.Updater;

public class UpdateDialog extends Stage implements Initializable {

	@FXML
	private ListView<String> stageList;

	@FXML
	private CheckBox cbDropDB;
	
	@FXML
	private Button btnStart;
	
	@FXML
	private Button btnCancel;
	
	private Updater _updater;
	
	public UpdateDialog(Parent parent)
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UpdateDialog.fxml"));
		fxmlLoader.setController(this);
		try
        {
			_updater = new Updater();
			setTitle("����������");
            setScene(new Scene((Parent)fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		stageList.getItems().clear();
		stageList.getItems().add("������� ������: " + Main.version);
		stageList.getItems().add("����� ����������...");
		
		try {
			if (_updater.checkForUpdates()) {
				stageList.getItems().add("���������� ����� ������ ��������: " + _updater.newVersion);
				stageList.getItems().add("��� ������ ���������� ������� \"������\"");
			}
			else {
				stageList.getItems().add("����� ������ �� ������� �� ����������.");
			}
		}
		catch(Exception e) {
			stageList.getItems().add("�� ����� ������ ���������� ��������� ������");
			e.printStackTrace();
		}
	}
	
	@FXML
	void onStartAction(ActionEvent event)
	{
		stageList.getItems().add("��������...");
		if (!_updater.download(_updater.newVersion)) {
			stageList.getItems().add("�� ����� �������� ��������� ������ (��������� �������� ����� ��� ���������� � ��������������)");
			return;
		}
		stageList.getItems().add("���������...");
		if (!_updater.install(!cbDropDB.isSelected())) {
			stageList.getItems().add("�� ����� ��������� ��������� ������ (��������� �������� ����� ��� ���������� � ��������������)");
			return;
		}
		stageList.getItems().add("����������...");
		stageList.getItems().add("���������");

		Alert aboutDlg = new Alert(AlertType.INFORMATION);
		aboutDlg.setTitle("����������");
		aboutDlg.setHeaderText(null);
		String content = "������� ��� ������� ��������.\n��������� ������� � ���� ��� ��������� ������� ���������";
		aboutDlg.setContentText(content);
		aboutDlg.showAndWait();
		
	}
	
	@FXML
	void onCancelAction(ActionEvent event)
	{
		close();
	}
}
