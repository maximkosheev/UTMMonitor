package madmax.UTMMonitor.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import madmax.UTMMonitor.ArchivePoint;
import madmax.UTMMonitor.Main;
import madmax.UTMMonitor.Settings;
import madmax.UTMMonitor.db.Trend;

public class ChartTabController extends UIController {

	@FXML
	protected LineChart<String, Number> chart;
	@FXML
	protected CategoryAxis timeAxis;
	@FXML
	protected NumberAxis yAxis;
	
	protected Trend trend;
	// ������ ��� �������������� ����������
	protected Timer refreshTimer;
	// �������� ��� ����������� �������� ����������
	protected ImageView image;
	
	// ������ ��� �������
	protected XYChart.Series<String, Number> series;
	
	@Override
	public void initialize(Node ui)
	{
		super.initialize(ui);
		
		this.trend = null;
		
		chart.setAnimated(false);
		
		timeAxis.setLabel("�����");
		
		yAxis.setLabel("����������");
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(-1);
		yAxis.setUpperBound(2);
		yAxis.setTickUnit(1);
		yAxis.setMinorTickVisible(false);
		refreshTimer = new Timer();

		refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!Settings.autoRefresh)
					return;
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// ���� ���� ��������� ������ �������, �� ������ ����� ����������
						if (Main.stage.isShowing())
							refresh();
						else {
							System.out.println("windows is minimized");
						}
					}
				});
			}
		}, Settings.refreshPeriod, Settings.refreshPeriod);

		image = new ImageView(new Image("madmax/UTMMonitor/ui/resources/processing.gif"));
		((AnchorPane)ui).getChildren().add(image);
		image.setVisible(false);
		
	}
	
	public void setTrend(Trend trend)
	{
		this.trend = trend;
		series = new XYChart.Series<String, Number>();
		series.setName(trend.getPoint().description);
		chart.getData().add(series);
		refresh();
	}

	public void refresh()
	{
		Node legend = chart.lookup(".chart-legend");
		
		Bounds legendBounds = new BoundingBox(
			legend.getLayoutX(), 
			legend.getLayoutY(), 
			legend.getBoundsInParent().getWidth(), 
			legend.getBoundsInParent().getHeight()
		);

		image.setLayoutX(legendBounds.getMaxX() + 5);
		image.setLayoutY(legendBounds.getMaxY() + 2);
		
		try {
			long startTime = System.currentTimeMillis() - Settings.viewLastTime;

			// ������������ ��������� ����� ������� � ����������� ������� ���������� ������
			if (startTime < trend.getStartTime())
				startTime = trend.getStartTime();
			
			// ������ ��� ����������� ���
			if (startTime < 0)
				return;
		
			image.setVisible(true);
			chart.setCursor(Cursor.WAIT);
			series.getData().clear();
			buildChart(trend, startTime, System.currentTimeMillis());
			chart.setCursor(null);
			image.setVisible(false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ���������� �������
	 * @param trend - �������� �����
	 * @param startTime - ��������� ����� �������
	 * @param endTime - �������� ����� �������
	 */
	protected void buildChart(Trend trend, long startTime, long endTime)
	{
		if (Settings.viewTimeTick > 0)
			buildChartByTimeStep(trend, startTime, endTime);
		else
			buildChartByIndexStep(trend, startTime, endTime);
	}
	
	/**
	 * ��������� ������� �� ���� ������ �� ������
	 * @param trend - �������� �����
	 * @param startTime - ��������� ����� �������
	 * @param endTime - �������� ����� �������
	 */
	protected void buildChartByIndexStep(Trend trend, long startTime, long endTime)
	{
		ArchivePoint archPoint = null;
		String timeVal = null;
		
		ArchivePoint pointStart = trend.getNearestPoint(startTime);
		
		if (pointStart == null)
			return;
		
		for (int nI = trend.findPoint(pointStart); nI < trend.getPointsCount(); nI++) {
			archPoint = trend.getPointByIndex(nI);
			timeVal = archPoint.getTime("yyyy-MM-dd HH:mm:ss");
			series.getData().add(new XYChart.Data<String, Number>(timeVal, getYValue(archPoint.value)));
		}
	}
	
	/**
	 * ��������� ������� �� ������ �� ������, ������ ����� ������������ �������� �������
	 * @param trend - �������� �����
	 * @param startTime - ��������� ����� �������
	 * @param endTime - �������� ����� �������
	 */
	protected void buildChartByTimeStep(Trend trend, long startTime, long endTime)
	{
		ArchivePoint archPoint = null;
		String timeVal = null;
		
		ArchivePoint pointStart = trend.getNearestPoint(startTime);
		
		if (pointStart == null)
			return;
		
		long currTime = startTime;
		while (currTime < endTime) {
			archPoint = trend.getNearestPoint(currTime);
			timeVal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currTime));
			series.getData().add(new XYChart.Data<String, Number>(timeVal, getYValue(archPoint.value)));
			currTime += Settings.viewTimeTick;
		}
		// ������ �������� �����, �������������� endTime �������
		archPoint = trend.getNearestPoint(endTime);
		timeVal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(endTime));
		series.getData().add(new XYChart.Data<String, Number>(timeVal, getYValue(archPoint.value)));
	}
	
	/**
	 * ������ �� ���� ����� ��� �������.
	 * ����� ��� ���������� ������� ��������� ����� ������� �� ������� � ������ ������� ������� 
	 * @param value
	 * @return
	 */
	protected double getYValue(double value)
	{
		if (value == 0)
			return 0;
		else if (value == 1)
			return 1;
		return -1;
	}
}
