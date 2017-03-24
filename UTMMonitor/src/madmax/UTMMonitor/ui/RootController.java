package madmax.UTMMonitor.ui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.WindowEvent;
import madmax.UTMMonitor.Main;
import madmax.UTMMonitor.Settings;

public class RootController {
	@FXML
	MenuBar menuBar;
	@FXML
	MenuItem menuQuit;
	@FXML
	MenuItem menuAbout;
	@FXML
	MenuItem menuCurl;
	@FXML
	MenuItem menuRefresh;
	@FXML
	CheckMenuItem menuAutoRefresh;
	@FXML
	MenuItem menuSettings;
	@FXML
	MenuItem menuUpdate;
	@FXML
	Button btnMinimizeTray;
	
	//private Timer notificationTimer = new Timer();
	public final String appIconLoc = "/madmax/UTMMonitor/ui/resources/appIcon.png";
	public final String trayIconLoc = "/madmax/UTMMonitor/ui/resources/trayIcon.png";
	
	public ChartsOverviewController mainFrameController;
	
	public void initialize()
	{
		menuQuit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Main.stage.fireEvent(new WindowEvent(Main.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			}
		});

		menuAbout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert aboutDlg = new Alert(AlertType.INFORMATION);
				aboutDlg.setTitle("О программе");
				aboutDlg.setHeaderText(null);
				String content = "Монитор УТМ - программа монитора работоспособности рабочего места УТМ\n";
				content += "Версия: " + Main.version + "\n";
				content += "Разработано по заказу KT: Алкоголь.\n";
				content += "Разработчики: Кощеев Максим (maximkosheev@gmail.com)";
				aboutDlg.setContentText(content);
				aboutDlg.showAndWait();
			}
		});
		
		menuCurl.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("onMenuCurl");
			}
		});

		menuRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mainFrameController.refreshActiveChart();
			}
		});

		menuSettings.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("onMenuSettings");
			}
		});
		
		menuAutoRefresh.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Settings.autoRefresh = menuAutoRefresh.isSelected();
			}
		});
		
		menuUpdate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onUpdate();
			}
		});
		
		btnMinimizeTray.setOnAction(handler -> {
			Main.stage.hide();
		});
		
		// главная иконка приложения
		Main.stage.getIcons().add(new Image(Main.class.getResourceAsStream(appIconLoc)));
		
		/* добавляем иконку в трей */
		try {
			java.awt.Toolkit.getDefaultToolkit();
			
			// проверяем доступность системного трея
			if (!java.awt.SystemTray.isSupported()) {
				System.out.println("System tray is not supported");
				// делаем кнопку "Свернуть в трей недоступной"
				btnMinimizeTray.setVisible(false);
			}
			
			java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
			
			URL imageLoc = Main.class.getResource(trayIconLoc);
			
			java.awt.Image icon = ImageIO.read(imageLoc);
			java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(icon);
			
			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));
			
			java.awt.MenuItem openItem = new java.awt.MenuItem("Открыть");
			java.awt.Font defaultFont = java.awt.Font.decode(null);
			java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
			openItem.setFont(boldFont);
			openItem.addActionListener(event -> Platform.runLater(this::showStage));
			
			java.awt.MenuItem exitItem = new java.awt.MenuItem("Выход");
			exitItem.addActionListener(event -> {
				Main.stage.fireEvent(new WindowEvent(Main.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
				tray.remove(trayIcon);
			});
			
			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(openItem);
			popup.addSeparator();
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);
			/*
			javax.swing.SwingUtilities.invokeLater(() -> {
				trayIcon.displayMessage("Hello",
					"The time is now: " + new SimpleDateFormat().format(new Date()),
					java.awt.TrayIcon.MessageType.INFO
				);
			});
			*/
			tray.add(trayIcon);
		}
		catch (java.awt.AWTException | IOException e) {
			System.out.println("Unable to init system tray");
			e.printStackTrace();
		}

		Main.newVersionAvailableHandle = new Runnable() {
			public void run() {
				Alert updateNotification = new Alert(AlertType.CONFIRMATION);
				updateNotification.setTitle("Информация");
				updateNotification.setHeaderText(null);
				String content = "Достуна новая версия приложения.\nОбновить приложение сейчас?";
				updateNotification.setContentText(content);
				Button btnOk = (Button)updateNotification.getDialogPane().lookupButton(ButtonType.OK);
				btnOk.setText("Да");
				Button btnCancel = (Button)updateNotification.getDialogPane().lookupButton(ButtonType.CANCEL);
				btnCancel.setText("Нет");
				Optional<ButtonType> result = updateNotification.showAndWait();
				
				if (result.get() == ButtonType.OK) {
					onUpdate();
				}
			}
		};
	}
	
	private void showStage()
	{
		Main.stage.show();
		Main.stage.toFront();
	}
	
	private void onUpdate()
	{
		UpdateDialog dlg = new UpdateDialog(null);
		dlg.showAndWait();
	}
}
