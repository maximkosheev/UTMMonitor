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
			setTitle("Обновление");
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
		stageList.getItems().add("Текущая версия: " + Main.version);
		stageList.getItems().add("Поиск обновлений...");
		
		try {
			if (_updater.checkForUpdates()) {
				stageList.getItems().add("Обнаружена новая версия монитора: " + _updater.newVersion);
				stageList.getItems().add("Для начала обновления нажмите \"Начать\"");
			}
			else {
				stageList.getItems().add("Новых версий на сервере не обнаружено.");
			}
		}
		catch(Exception e) {
			stageList.getItems().add("Во время поиска обновления произошла ошибка");
			e.printStackTrace();
		}
	}
	
	@FXML
	void onStartAction(ActionEvent event)
	{
		stageList.getItems().add("Загрузка...");
		if (!_updater.download(_updater.newVersion)) {
			stageList.getItems().add("Во время загрузки произошла ошибка (повторите операцию позже или обратитесь к администратору)");
			return;
		}
		stageList.getItems().add("Установка...");
		if (!_updater.install(!cbDropDB.isSelected())) {
			stageList.getItems().add("Во время установки произошла ошибка (повторите операцию позже или обратитесь к администратору)");
			return;
		}
		stageList.getItems().add("Завершение...");
		stageList.getItems().add("Выполнено");

		Alert aboutDlg = new Alert(AlertType.INFORMATION);
		aboutDlg.setTitle("Информация");
		aboutDlg.setHeaderText(null);
		String content = "Монитор УТМ успешно обновлен.\nИзменения вступят в силу при следующем запуске программы";
		aboutDlg.setContentText(content);
		aboutDlg.showAndWait();
		
	}
	
	@FXML
	void onCancelAction(ActionEvent event)
	{
		close();
	}
}
