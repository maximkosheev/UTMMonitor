package madmax.UTMMonitor.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import madmax.UTMMonitor.ArchiveDataController;
import madmax.UTMMonitor.ArchivePoint;
import madmax.UTMMonitor.Settings;

/**
 *  Экземпляр данного класса содержит текущее состояниие
 *  параметров системы UTM, за которыми выполняется мониторинг 
 * @author MadMax
 *
 */
public class DBController {
	private static DBController _instance = null;

	// параметры, за которыми выполняется мониторинг
	private List<DBPoint> _points;
	// контроллер архива
	protected ArchiveDataController archive;
	// таймер для периодического архивирования параметров
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
	 * Создает точку в базе данных
	 * @param paramId int уникальный идентификатор параметра в бд
	 * @param paramName String - символическое имя параметра
	 * @return DBPoint - созданный параметр в БД
	 */
	public DBPoint createPoint(int paramId, String paramName)
	{
		DBPoint point = new DBPoint(paramId, paramName);
		_points.add(point);
		return point;
	}
	
	/**
	 * Возвращает точку в БД по ее имени
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
	 * Возвращает точку в БД по ее имени
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
	 * Изменяет значение параметра в базе
	 * @param paramName - String имя параметра в БД 
	 * @param value - int новое значение параметра
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
	 * Возвращает список всех точек БД
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
