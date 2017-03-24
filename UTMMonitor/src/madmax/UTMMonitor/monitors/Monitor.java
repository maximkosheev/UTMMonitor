package madmax.UTMMonitor.monitors;

import madmax.UTMMonitor.db.DBController;

/**
 * ������� ����� ��� ���� ���������
 * @author MadMax
 *
 */
public class Monitor extends Thread implements IMonitor {

	// ����� � ��, �������� ������� �������� ���������, �� ������� ������ ������ ������� 
	protected String dbPoint;
	
	/**
	 * �����������
	 * @param point - ����� � ��
	 */
	public Monitor(String dbPoint)
	{
		this.dbPoint = dbPoint;
	}
	
	public String getPoint()
	{
		return dbPoint;
	}
	
	public void set()
	{
		DBController.getInstance().updatePoint(dbPoint, 1);
	}
	
	public void reset()
	{
		DBController.getInstance().updatePoint(dbPoint, 0);
	}
}
