package madmax.UTMMonitor;
	
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import madmax.UTMMonitor.db.DBController;
import madmax.UTMMonitor.db.DBPoint;
import madmax.UTMMonitor.monitors.DeviceMonitor;
import madmax.UTMMonitor.monitors.FileSystemMonitor;
import madmax.UTMMonitor.monitors.Monitor;
import madmax.UTMMonitor.monitors.MonitorsFactory;
import madmax.UTMMonitor.monitors.ProcessMonitor;
import madmax.UTMMonitor.monitors.WebServerMonitor;
import madmax.UTMMonitor.ui.ChartsOverviewController;
import madmax.UTMMonitor.ui.RootController;


public class Main extends Application {
	
	private static DBController _db;
	private static ArrayList<Monitor> _monitors;
	private RootController rootController;

	// каталог из которого поизводитс€ запуск приложени€
	public static String __DIR__;
	
	// текуща€ верси€ приложени€ (задаетс€ вручную)
	public static final String version = "1.1-0";
	// признак того, что на сервере обновлений есть нова€ верси€
	public static boolean newVersionAvailable = false;
	//...
	public static Runnable newVersionAvailableHandle = null;
	
	public static Stage stage;
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			
			File jarFile = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile();
			
			Main.__DIR__ = jarFile.getParentFile().getPath();
			
			System.out.println("UTMMonitor v" + Main.version);
			
			System.out.println("Working dir: " + Main.__DIR__);
			
			stage = primaryStage;
			
			Platform.setImplicitExit(false);
			
			// создание экзепл€ра базы данных
			_db = DBController.getInstance();

			// массив потоков мониторинга
			_monitors = new ArrayList<Monitor>();

			// чтение конфигурационного файла
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = dbf.newDocumentBuilder();
				Document doc = docBuilder.parse("config.xml");
				Element configNode = doc.getDocumentElement();
				
				// „тение информации о параметрах Ѕƒ
				Node paramsNode = configNode.getElementsByTagName("params").item(0);
				Node paramNode = paramsNode.getFirstChild();
				while(paramNode != null) {
					if (paramNode instanceof Element) {
						String attrId = ((Element) paramNode).getAttribute("id");
						String attrName = ((Element) paramNode).getAttribute("name");
						String attrShortname = ((Element) paramNode).getAttribute("shortname");
						String attrDesct = ((Element) paramNode).getAttribute("descr");
						DBPoint dbPoint = _db.createPoint(Integer.parseInt(attrId), attrName);
						dbPoint.shortname = attrShortname;
						dbPoint.description = attrDesct;
					}
					paramNode = paramNode.getNextSibling();
				}
				
				// чтение информации о мониторах
				Node monitorsNode = configNode.getElementsByTagName("monitors").item(0);
				//String sleepTime = ((Element)monitorsNode).getAttribute("sleepTime");
				Node monitorNode = monitorsNode.getFirstChild();
				while (monitorNode != null) {
					if (monitorNode instanceof Element) {
						String attrParamName = ((Element) monitorNode).getAttribute("param");
						String attrMonitorType = ((Element) monitorNode).getAttribute("type").toLowerCase();
						DBPoint dbPoint = _db.getPoint(attrParamName);
				
						// контроль процесса
						if (attrMonitorType.equals("process")) {
							ProcessMonitor monitor = MonitorsFactory.createProcessMonitor(dbPoint.name, monitorNode.getTextContent());
							_monitors.add(monitor);
						}
						// контроль web-сервера
						else if (attrMonitorType.equals("webserver")) {
							WebServerMonitor monitor = MonitorsFactory.createWebServerMonitor(dbPoint.name, monitorNode.getTextContent());
							_monitors.add(monitor);
						}
						else if (attrMonitorType.equals("filesystem")) {
							FileSystemMonitor monitor = MonitorsFactory.createFileSystemMonitor(dbPoint.name, monitorNode.getTextContent());
							_monitors.add(monitor);
						}
						else if (attrMonitorType.equals("device")) {
							Node vidNode = ((Element) monitorNode).getElementsByTagName("vid").item(0);
							Node pidNode = ((Element) monitorNode).getElementsByTagName("pid").item(0);
							int vid = Integer.parseUnsignedInt(vidNode.getTextContent(), 16);
							int pid = Integer.parseUnsignedInt(pidNode.getTextContent(), 16);
							DeviceMonitor monitor = MonitorsFactory.createDeviceMonitor(dbPoint.name, vid, pid);
							_monitors.add(monitor);
						}
						else {
							// TODO Add other monitors...
						}
					}
					monitorNode = monitorNode.getNextSibling();
				}

				// чтение параметров, относ€щихс€ к архивации
				Node archiveNode = configNode.getElementsByTagName("archive").item(0);
				if (archiveNode != null) {
					Node archiveDir = ((Element)archiveNode).getElementsByTagName("directory").item(0);
					if (archiveDir != null)
						Settings.archiveDir = archiveDir.getTextContent();
				}
				
				// „тение параметров, относ€щихс€ к отображению графика
				Node chartNode = configNode.getElementsByTagName("chart").item(0);
				if (chartNode != null) {
					Node refreshPeriodNode = ((Element)chartNode).getElementsByTagName("refresh-period").item(0);
					if (refreshPeriodNode != null)
						Settings.refreshPeriod = Integer.parseInt(refreshPeriodNode.getTextContent()) * 1000;

					Node viewLastTimeNode = ((Element)chartNode).getElementsByTagName("viewlasttime").item(0);
					if (viewLastTimeNode != null)
						Settings.viewLastTime = Integer.parseInt(viewLastTimeNode.getTextContent()) * 1000;
					
					Node viewTimeTickNode = ((Element)chartNode).getElementsByTagName("timetick").item(0);
					if (viewTimeTickNode != null)
						Settings.viewTimeTick = Integer.parseInt(viewTimeTickNode.getTextContent()) * 1000;
					else
						Settings.viewTimeTick = 0;
					
					Node autoRefreshNode = ((Element)chartNode).getElementsByTagName("autorefresh").item(0);
					if (autoRefreshNode != null)
						Settings.autoRefresh = Boolean.parseBoolean(autoRefreshNode.getTextContent());
				}
			}
			catch(UnsupportedOSTypeException e) {
				System.out.println("Unsupported operating system");
				return;
			}
			catch (Exception e) {
				System.out.println("config.xml: Error while parsing file");
				return;
			}

			// запуск контроллеров Ѕƒ и архива
			_db.start();
			
			// дл€ каждого архивного тренда задаем начальную временную метку
			for (DBPoint point : _db.getAllPoints()) {
				point.trend.setStartTime(System.currentTimeMillis() - Settings.viewLastTime);
			}
			
			// восстановление архивных данных из архива
			_db.readArchiveData();
			
			// запуск потоков мониторинга
			for(Monitor monitor : _monitors)
			{
				monitor.setName(monitor.getPoint());
				monitor.start();
			}

			FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/RootLayout.fxml"));
			BorderPane rootLayout = (BorderPane)loader.load();
			Scene scene = new Scene(rootLayout, 800, 600);
			String appCss = getClass().getResource("application.css").toExternalForm();
			scene.getStylesheets().add(appCss);
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					// приказываем потокам мониторинга остановитьс€
					for (Monitor monitor : _monitors) {
						monitor.interrupt();
					}
					Platform.exit();
					System.exit(0);
				}
			});
			primaryStage.show();
			rootController = loader.getController();
			// вызывать этот метод не об€зательно :))
			//  ак оказалось, в JavaFx при загрузке контроллера сам ищет метод без параметров и называющийс€ initialize
			// см FXMLLoader.java loadImpl (строка 2563)
			// rootController.initialize();
			showCharts(rootLayout, _db.getAllPoints());
			
			// запускаем таймер проверки обновлени€ (проверка обновлени€ раз в 12 часов)
			Timer updateCheckerTimer = new Timer();
			updateCheckerTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					Updater updater = new Updater();
					try {
						if (updater.checkForUpdates()) {
							Main.newVersionAvailable = true;
							if (newVersionAvailableHandle != null)
								Platform.runLater(newVersionAvailableHandle);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 5000, 43200000);
		}
		catch(Exception e) {
			if (e.getMessage() != null)
				System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void showCharts(BorderPane parent, List<DBPoint> points)
	{
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("ui/ChartsOverview.fxml"));
			AnchorPane windowContent = (AnchorPane)loader.load();
			
			windowContent.prefWidthProperty().bind(parent.widthProperty());
			windowContent.prefHeightProperty().bind(parent.heightProperty());
			
			parent.setCenter(windowContent);
			
			ChartsOverviewController controller = loader.getController();
			controller.intitialize();
			rootController.mainFrameController = controller;
			
			for (DBPoint point : points) {
				controller.createTab(point);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
