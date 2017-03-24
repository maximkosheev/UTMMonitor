package madmax.UTMMonitor;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import madmax.UTMMonitor.db.DBPoint;

/**
 * Контроллер для работы с архивными данными
 * @author MadMax
 *
 */
public class ArchiveDataController {
	
	private static ArchiveDataController _instance = null;
	
	// поток для записи архивных данных текущей сессии
	private DataOutputStream _dos;
	// менеджер файлов, содержащих архивные данные
	private ArchiveFilesManager _filesManager;
	
	private ArchiveDataController()
	{
		_instance = this;
		_filesManager = ArchiveFilesManager.getInstance();
	}
	
	public static ArchiveDataController getInstance()
	{
		if (_instance == null)
			_instance = new ArchiveDataController();
		return _instance;
	}
	
	public void initialize()
	{
		_filesManager.initialize();
	}
	
	public void start()
	{
		// создает архивный файл для текущей сессии
		try {
			String filename = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".arch";
			
			filename = Settings.archiveDir + "/" + filename;

			_dos = new DataOutputStream(new FileOutputStream(filename));
			
			int senior = 1;
			int minor = 0;
			int version = (senior << 16) + minor;
			// запись версии формата архивного файла
			_dos.writeInt(version);
			// запись временной метки начала файла
			_dos.writeLong(System.currentTimeMillis());
		}
		catch (Exception e) {
			//
		}
	}
	
	/**
	 * Добавляет новую точку к архивному тренду
	 * @param DBPoint point - имя параметра, для которого добавляется новая точка
	 * @param ArchivePoint archPoint - данные о точке
	 */
	public void addArchivePoint(DBPoint point, ArchivePoint archPoint)
	{
		try {
			// добавляем точку к архивному тренду
			point.trend.appendPoint(archPoint);
			/* запись точки в архив */
			// запись идентификатора параметра
			_dos.writeInt(point.id);
			// запись временной метки
			_dos.writeLong(archPoint.time);
			// запись значения
			_dos.writeInt(archPoint.value);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Восстанавливает архивные данные из архивных файлов
	 * @param paramName - имя параметра
	 */
	public void restoreArchiveData(DBPoint point)
	{
		try {
			ArchiveFile file = _filesManager.getFirst();
			while (file != null) {
				// открываем файл и читаем все архивные данные, относящиеся к данному параметру
				ArchiveInputStream is = new ArchiveInputStream(file);
				// чтение заголовка version + time
				is.readHeader();
				/* чтение архивных данных */
				ArchivePoint archPoint = null;
				while ((archPoint = is.readPoint()) != null) {
					// архивные данные относятся к параметру, который обрабатывается в данный момент
					if (archPoint.paramId == point.id) {
						point.trend.appendPoint(archPoint);
					}
				}
				// закрываем архивный файл
				is.close();
				// берем следующий файл
				file = _filesManager.getNext();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
