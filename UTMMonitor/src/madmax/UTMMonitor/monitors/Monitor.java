package madmax.UTMMonitor.monitors;

import madmax.UTMMonitor.db.DBController;

/**
 * Базовый класс для всех мониторов
 * @author MadMax
 *
 */
public class Monitor extends Thread implements IMonitor {

	// точка в БД, хранящая текущее значение параметра, за которым следит данный монитор 
	protected String dbPoint;
	
	/**
	 * Конструктор
	 * @param point - точка в БД
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
