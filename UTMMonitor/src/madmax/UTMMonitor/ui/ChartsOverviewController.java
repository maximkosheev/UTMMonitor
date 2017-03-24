package madmax.UTMMonitor.ui;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import madmax.UTMMonitor.Main;
import madmax.UTMMonitor.db.DBPoint;

public class ChartsOverviewController {

	@FXML
	private TabPane tabs;
	
	private Map<Tab, ChartTabController> tabControllers;

	public void intitialize()
	{
		tabControllers = new HashMap<Tab, ChartTabController>();
	}
	
	/**
	 * ������� �������, � ������� ����� ������������ ������ ���������
	 * @param point DBPoint - ��������
	 */
	public void createTab(DBPoint point)
	{
		try {
			// ��������� ��� ������� (����������� ������� ��� ������� ���������)
			String resourceName = String.format("ui/%s_ChartOverview.fxml", point.name);
			// �������� ������ �� �������� �������
			URL resource = Main.class.getResource(resourceName);

			// ���� ������ � ��������� ����������� ������� �� ������, ���������� ������ �� ���������
			if (resource == null)
				resource = Main.class.getResource("ui/ChartOverview.fxml");
			
			FXMLLoader loader = new FXMLLoader(resource);
	
			AnchorPane content = (AnchorPane)loader.load();
			
			ChartTabController controller = loader.getController();
			controller.initialize(content);
			controller.setTrend(point.trend);
	
			final Tab tab = new Tab(point.shortname);
	
			content.prefWidthProperty().bind(tabs.widthProperty());
			content.prefHeightProperty().bind(tabs.heightProperty());
			
			tab.setContent(content);
			
			tabs.getTabs().add(tab);
			tabControllers.put(tab, controller);

		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void refreshActiveChart()
	{
		ChartTabController controller = tabControllers.get(tabs.getSelectionModel().getSelectedItem());
		controller.refresh();
	}
}
