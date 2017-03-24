package madmax.UTMMonitor.db;

/**
 * Описывает параметр системы, за которым ведется мониторинг
 * @author MadMax
 *
 */
public class DBPoint {
	public int id;
	public String name;
	public String shortname;
	public String description;
	public int value;
	// архивные данные
	public Trend trend;
	
	public DBPoint(int id, String name)
	{
		this.id = id;
		this.name = name;
		this.shortname = "";
		this.description = "";
		this.value = -1;
		this.trend = new Trend(this);
	}
}
