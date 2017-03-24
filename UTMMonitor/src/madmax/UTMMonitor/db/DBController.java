package madmax.UTMMonitor.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import madmax.UTMMonitor.ArchiveDataController;
import madmax.UTMMonitor.ArchivePoint;
import madmax.UTMMonitor.Settings;

/**
 *  ��������� ������� ������ �������� ������� ����������
 *  ���������� ������� UTM, �� �������� ����������� ���������� 
 * @author MadMax
 *
 */
public class DBController {
	private static DBController _instance = null;

	// ���������, �� �������� ����������� ����������
	private List<DBPoint> _points;
	// ���������� ������
	protected ArchiveDataController archive;
	// ������ ��� �������������� ������������� ����������
	protected Timer dumpTimer;
	
	private DBController()
	{
		_instance = this;

		_points = new ArrayList<DBPoint>();
		archive = ArchiveDataController.getInstance();
		
		dumpTimer = new Timer(true);
	}

	public void start()
	{
		archive.initialize();
		archive.start();
		dumpTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				for (DBPoint point : _points) {
					archive.addArchivePoint(point, new ArchivePoint(point.id, point.value, System.currentTimeMillis()));
				}
			}
		}, Settings.dumpPeriod, Settings.dumpPeriod);
	}
	
	/**
	 * ������� ����� � ���� ������
	 * @param paramId int ���������� ������������� ��������� � ��
	 * @param paramName String - ������������� ��� ���������
	 * @return DBPoint - ��������� �������� � ��
	 */
	public DBPoint createPoint(int paramId, String paramName)
	{
		DBPoint point = new DBPoint(paramId, paramName);
		_points.add(point);
		return point;
	}
	
	/**
	 * ���������� ����� � �� �� �� �����
	 * @param paramName
	 * @return
	 */
	public DBPoint getPoint(String paramName)
	{
		for (DBPoint point : _points) {
			if (point.name.equals(paramName))
				return point;
		}
		return null;
	}
	
	/**
	 * ���������� ����� � �� �� �� �����
	 * @param paramName
	 * @return
	 */
	public DBPoint getPoint(int paramID)
	{
		for (DBPoint point : _points) {
			if (point.id == paramID)
				return point;
		}
		return null;
	}

	/**
	 * �������� �������� ��������� � ����
	 * @param paramName - String ��� ��������� � �� 
	 * @param value - int ����� �������� ���������
	 */
	public void updatePoint(String paramName, int value)
	{
		DBPoint point = getPoint(paramName);

		if (point.value != value) {
			point.value = value;
			//archive.addArchivePoint(point, new ArchivePoint(value, System.currentTimeMillis()));
		}
	}
	
	/**
	 * ���������� ������ ���� ����� ��
	 * @return - List<DBPoint>
	 */
	public List<DBPoint> getAllPoints()
	{
		return _points;
	}
	
	/**
	 * 
	 */
	public void readArchiveData()
	{
		for (DBPoint point : _points) {
			archive.restoreArchiveData(point);
		}
	}
	
	public static DBController getInstance()
	{
		if (_instance == null)
			_instance = new DBController();
		return _instance;
	}
}
