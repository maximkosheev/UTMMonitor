package madmax.UTMMonitor;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import madmax.UTMMonitor.db.DBPoint;

/**
 * ���������� ��� ������ � ��������� �������
 * @author MadMax
 *
 */
public class ArchiveDataController {
	
	private static ArchiveDataController _instance = null;
	
	// ����� ��� ������ �������� ������ ������� ������
	private DataOutputStream _dos;
	// �������� ������, ���������� �������� ������
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
		// ������� �������� ���� ��� ������� ������
		try {
			String filename = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".arch";
			
			filename = Settings.archiveDir + "/" + filename;

			_dos = new DataOutputStream(new FileOutputStream(filename));
			
			int senior = 1;
			int minor = 0;
			int version = (senior << 16) + minor;
			// ������ ������ ������� ��������� �����
			_dos.writeInt(version);
			// ������ ��������� ����� ������ �����
			_dos.writeLong(System.currentTimeMillis());
		}
		catch (Exception e) {
			//
		}
	}
	
	/**
	 * ��������� ����� ����� � ��������� ������
	 * @param DBPoint point - ��� ���������, ��� �������� ����������� ����� �����
	 * @param ArchivePoint archPoint - ������ � �����
	 */
	public void addArchivePoint(DBPoint point, ArchivePoint archPoint)
	{
		try {
			// ��������� ����� � ��������� ������
			point.trend.appendPoint(archPoint);
			/* ������ ����� � ����� */
			// ������ �������������� ���������
			_dos.writeInt(point.id);
			// ������ ��������� �����
			_dos.writeLong(archPoint.time);
			// ������ ��������
			_dos.writeInt(archPoint.value);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��������������� �������� ������ �� �������� ������
	 * @param paramName - ��� ���������
	 */
	public void restoreArchiveData(DBPoint point)
	{
		try {
			ArchiveFile file = _filesManager.getFirst();
			while (file != null) {
				// ��������� ���� � ������ ��� �������� ������, ����������� � ������� ���������
				ArchiveInputStream is = new ArchiveInputStream(file);
				// ������ ��������� version + time
				is.readHeader();
				/* ������ �������� ������ */
				ArchivePoint archPoint = null;
				while ((archPoint = is.readPoint()) != null) {
					// �������� ������ ��������� � ���������, ������� �������������� � ������ ������
					if (archPoint.paramId == point.id) {
						point.trend.appendPoint(archPoint);
					}
				}
				// ��������� �������� ����
				is.close();
				// ����� ��������� ����
				file = _filesManager.getNext();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
